package com.example.justin.verbeterjegemeente.Presentation;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.example.justin.verbeterjegemeente.API.ConnectionChecker;
import com.example.justin.verbeterjegemeente.API.RequestManager;
import com.example.justin.verbeterjegemeente.API.ServiceClient;
import com.example.justin.verbeterjegemeente.API.ServiceGenerator;
import com.example.justin.verbeterjegemeente.Business.BitmapGenerator;
import com.example.justin.verbeterjegemeente.Constants;
import com.example.justin.verbeterjegemeente.R;
import com.example.justin.verbeterjegemeente.domain.ServiceRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.justin.verbeterjegemeente.Constants.CONNECTION_FAILURE_RESOLUTION_REQUEST;
import static com.example.justin.verbeterjegemeente.Constants.DEFAULT_LAT;
import static com.example.justin.verbeterjegemeente.Constants.DEFAULT_LONG;

// TODO: 8-8-2017 commented some code for google map
public class Tab1Fragment extends /*SupportMapFragment*/ Fragment implements /*OnMapReadyCallback,*/ GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener, GoogleMap.OnCameraIdleListener,
        RequestManager.OnServiceRequestsReady {
    public GoogleMap mMap;
    private Location currentLocation;
    public LatLng currentLatLng;
    public GoogleApiClient mApiClient;
    ServiceClient client;
    private ServiceRequestsReadyListener sRequestCallback;
    private String currentRadius;
    private String servCodeQ;
    private boolean eersteKeer;
    public float zoomLevel;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;

    public void onCreate(Bundle savedInstaceState) {
        super.onCreate(savedInstaceState);

        eersteKeer = true;
        // get user selected radius and cat or use default radius and cat
        SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        int rValue = prefs.getInt(getString(R.string.activityMain_saved_radius), 20); // 20 is default
        currentRadius = Integer.toString(rValue);

        servCodeQ = prefs.getString(getString(R.string.activityMain_saved_servcodeQ), null);

        client = ServiceGenerator.createService(ServiceClient.class);

        // TODO: 8-8-2017 replaced api here
//        initApi();
        buildGoogleApiClient();
        createLocationRequest();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            sRequestCallback = (ServiceRequestsReadyListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        /*try {
            if (!ConnectionChecker.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mApiClient, this);
                if (mApiClient != null) {
                    mApiClient.disconnect();

                }
            }
        } catch (Exception e) {
            Log.i("EXCEPTION: ", e.getLocalizedMessage());
        }*/

        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            if (ConnectionChecker.isConnected()) {
                mApiClient.connect();
            } else {
                new AlertDialog.Builder(this.getContext())
                        .setTitle("No Internet Connection")
                        .setMessage("It looks like your internet connection is off. Please turn it " +
                                "on and try again")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // user will return to Tab1Fragment
                            }
                        }).setIcon(android.R.drawable.ic_dialog_alert).show();
            }
        } catch (Exception e) {
            Log.i("Exception: ", e.getLocalizedMessage());
        }
        // TODO: 8-8-2017 googlemap commented
// commented for map invisible
//        setUpMapIfNeeded();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mApiClient, this);
            mApiClient.disconnect();// TODO: 8-8-2017 googlemap commented
        }
    }

    //set up map when map is loaded
    // TODO: 8-8-2017 googlemap commented
   /* public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnCameraIdleListener(this);


        //get Current radius selected by user in MainActivity
        Log.i("Current radius: ", currentRadius);

        setUpMap();
        Log.e("MAP: ", "map is klaargezet");
    }*/


    /**
     * Opzetten van de map
     */
    // TODO: 8-8-2017 googlemap commented
    /*private void setUpMap() {
        //setup map settings
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setPadding(60, 100, 0, 180);

        initApi();

    }*/

    /**
     * Connectie maken met de Google Api
     */
    public void initApi() {
        mApiClient = new GoogleApiClient.Builder(this.getContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        mApiClient.connect();

        SharedPreferences prefs = getActivity().getPreferences(getContext().MODE_PRIVATE);
        String eersteAanvraag = prefs.getString("eersteAanvraag", "true");

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (eersteAanvraag.equalsIgnoreCase("true")) {
                reqFindLocation();
            } else {
                getDefaultLocation();
            }
        } else {
            getUserLocation();
        }
        prefs.edit().putString("eersteAanvraag", "false").apply();
    }

    /**
     * Locatie van de gebuiker ophalen
     */
    public void getUserLocation() {
        /*LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        builder.setAlwaysShow(true);


        final PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {

            @Override
            public void onResult(@NonNull LocationSettingsResult LSresult) {
                final Status status = LSresult.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            *//*currentLocation = LocationServices.FusedLocationApi.getLastLocation(mApiClient);
                            if (currentLocation != null) {
                                eersteKeer = false;
                                currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                                // TODO: 8-8-2017 googlemap commented
//                                CameraUpdate center = CameraUpdateFactory.newLatLngZoom(currentLatLng, 16.0f);

                                // see what current location is
                                Log.d("Tab1Fragment: ", "lat: " + currentLocation.getLatitude() + " long: " + currentLocation.getLongitude());
                                // TODO: 8-8-2017 googlemap commented
//                                mMap.moveCamera(center);
                            } else {
                                if (eersteKeer = true){
                                    getLocation();
                                }
                                Log.e("getUserLocation", "Kan locatie niet ophalen");
                            }*//*
                            mFusedLocationClient.getLastLocation().addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    if (eersteKeer = true) {
                                        getDefaultLocation();
                                    }
                                    Log.e("getUserLocation", "Kan locatie niet ophalen");
                                }
                            })
                                    .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                                        @Override
                                        public void onSuccess(Location location) {
                                            // Got last known location. In some rare situations this can be null.
                                            if (location != null) {
                                                eersteKeer = false;
                                                // see what current location is
                                                Log.d("Tab1Fragment: ", "lat: " + location.getLatitude() + " long: " + location.getLongitude());
                                            }
                                        }
                                    });

                        } else {
                            Log.e("getUserLocation", "Geen toestemming");
                        }
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(getActivity(), Constants.REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:

                        break;
                }
            }
        });*/
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // nothing has to be done here

        /*mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // ...
                        }
                    }
                });*/

        // !! gets called everytime request permission dialog closes
        getLastLocation();




    }

    /**
     * Standaard locatie tonen eerste keer laden van map als er geen toestemming gegegeven is
     */
    public void getDefaultLocation() {
        // TODO: 8-8-2017 googlemap commented
//        CameraUpdate center = CameraUpdateFactory.newLatLngZoom(new LatLng(DEFAULT_LAT, DEFAULT_LONG), 12.0f);

        // see what current location is
        Log.d("Tab1Fragment: ", "default location is choosen");
        // TODO: 8-8-2017 googlemap commented
//        mMap.moveCamera(center);
        eersteKeer = false;
    }

    public void getLastLocation() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    Constants.MY_PERMISSIONS_LOCATION);
        } else {
            Location location = LocationServices.FusedLocationApi.getLastLocation(mApiClient);
            if (location == null) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mApiClient, mLocationRequest, this);
            }
            else {
                Log.d("Tab1Fragment: ", location.toString());
            };
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        // nothing has to happen here
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(getActivity(), CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(" Tab1Fragment", "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("Tab1Fragment: ", "updated location: " + location.toString());
    }

    /**
     * Als de map bewogen wordt de nieuwe locatie meegeven aan de listener
     */
    @Override
    public void onCameraIdle() {
        //get Current radius selected by user in MainActivity
        Log.i("Current radius: ", currentRadius);

        LatLng center = mMap.getCameraPosition().target;
        String camLat = Double.toString(center.latitude);
        String camLng = Double.toString(center.longitude);

        if (!eersteKeer && (Double.compare(center.latitude, Constants.DEFAULT_LAT) != 0 ||
                Double.compare(center.longitude, Constants.DEFAULT_LONG) != 0)) {

            currentLatLng = new LatLng(center.latitude, center.longitude);

            zoomLevel = mMap.getCameraPosition().zoom;
        }

        Log.i("Camera positie: ", "is veranderd");

        // from here all the API requests will be handled
        RequestManager reqManager = new RequestManager(getActivity());
        // set callback for data passing
        reqManager.setOnServiceReqReadyCallb(this);

        Log.i("servCodeq: ", "" + servCodeQ);

        if (servCodeQ != null && !servCodeQ.equals("")) {
            // launch Retrofit callback and retrieve services asynchronously
            reqManager.getServiceRequests(camLat, camLng, null, currentRadius, servCodeQ);
        } else {
            // launch Retrofit callback and retrieve services asynchronously
//            reqManager.getServiceRequests(camLat, camLng, null, currentRadius, servCodeQ);
            reqManager.getServiceRequests(camLat, camLng, null, currentRadius);
        }

    }

    /**
     * Permissie wordt gevraagd als dit niet al gegeven is
     */
    public void reqFindLocation() {
        /*if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    Constants.MY_PERMISSIONS_LOCATION);
        } else {
            getUserLocation();
        }*/
    }

    protected synchronized void buildGoogleApiClient() {
        Log.i("Tab1Fragment", "Building GoogleApiClient");

        mApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

    }

    protected void createLocationRequest() {

        //import should be **import com.google.android.gms.location.LocationListener**;
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(30 * 1000);
        mLocationRequest.setFastestInterval(5 * 1000);


    }

    /**
     * Controleren of permissies goed gekeurd zijn door de gebruiker
     *
     * @param requestCode  meegegeven activiteit nummer die gedaan is
     * @param permissions  permissies die aangevraagd worden
     * @param grantResults hoeveelheid permissies die goed gekeurd zijn door de gebruiker
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Constants.MY_PERMISSIONS_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Permissie gekregen
//                    getUserLocation();
                    getLastLocation();
                } else {
                    Log.i("onRequestPermResult", "Geen toestemming gekregen, eerste keer dat map geladen wordt default lat/long gepakt");
                    if (currentLatLng == null) {
                        getDefaultLocation();
                    }
                    new AlertDialog.Builder(this.getContext())
                            .setTitle(getString(R.string.eFindLocationTitle))
                            .setMessage(getString(R.string.eFindLocation))
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // send user back to Tab1Fragment
                                }
                            }).setIcon(android.R.drawable.ic_dialog_alert).show();
                }
            }
        }
    }
    
    /**
     * Permissie wordt gevraagd als dit niet al gegeven is
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.REQUEST_CHECK_SETTINGS:
                if (resultCode == Activity.RESULT_OK) {
                    reqFindLocation();
                } else {
                   new AlertDialog.Builder(this.getContext())
                        .setTitle(getString(R.string.eFindLocationTitle))
                       .setMessage(getString(R.string.eFindLocation))
                       .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                             }
                        }).setIcon(android.R.drawable.ic_dialog_alert).show();

                    if (currentLatLng == null){
                        getDefaultLocation();
                    }

            }
            Log.i("onActivityResult", "Gps aanvraag afgewezen");
        }
    }

    /**
     * This method will update the radius and service codes connected to the category
     * set by the user. After that it will get new service requests based on the new radius and
     * category filter and add them as markers on a Google map
     *
     * @param radius    radius in meters
     * @param servCodeQ String with service codes appending by a , delimiter
     *                  that can be used for filtering service requests.
     */
    public void updateRadiusCat(int radius, String servCodeQ) {
        String pRadius = Integer.toString(radius);
        currentRadius = pRadius;
        this.servCodeQ = servCodeQ;
        String currentLat;
        String currentLng;
        if (currentLatLng == null) {
            currentLat = Double.toString(Constants.DEFAULT_LAT);
            currentLng = Double.toString(Constants.DEFAULT_LONG);
        } else {
            currentLat = Double.toString(currentLatLng.latitude);
            currentLng = Double.toString(currentLatLng.longitude);
        }

        Log.i("servCodeq: ", "" + servCodeQ);

        // from here all the API requests will be handled
        RequestManager reqManager = new RequestManager(getActivity());
        // set callback for data passing
        reqManager.setOnServiceReqReadyCallb(this);

        if (servCodeQ != null && !servCodeQ.equals("")) {
            // launch Retrofit callback and retrieve services asynchronously
            reqManager.getServiceRequests(currentLat, currentLng, null, currentRadius, servCodeQ);
        } else {
            // launch Retrofit callback and retrieve services asynchronously
            reqManager.getServiceRequests(currentLat, currentLng, null, currentRadius);
        }

    }

    /**
     * Add every newly received <code>ServiceRequest</code> to the Google map as a marker.
     * @param srList list of service requests obtained with Retrofit from an open311 Interface.
     */
    public void addServRequestsToMap(ArrayList<ServiceRequest> srList) {
        // clear all markers on the map before adding new markers
        mMap.clear();

        // loop through all the service requests and add their description
        // to a new marker on the Google map
        for (ServiceRequest s : srList) {
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(s.getLat(), s.getLong()))
                    .title(s.getDescription())
                    .icon(BitmapDescriptorFactory.fromBitmap(
                            BitmapGenerator.getBitmapFromVectorDrawable(getContext(),
                                    R.drawable.service_request_marker)))
            );
        }
    }

    @Override
    public void serviceRequestsReady(ArrayList<ServiceRequest> serviceRequests) {
        // pass received Service Requests to ActivityMain
        // ActivityMain will then pass the requests to Tab2Fragment
        sRequestCallback.onServiceRequestsReady(serviceRequests);

        addServRequestsToMap(serviceRequests);
    }

    /**
     * Listener that passes a list of requested service requests to the MainActivity, so
     *  the MainActivity can pass them to Tab2Fragment.
     */
    public interface ServiceRequestsReadyListener {
        void onServiceRequestsReady(ArrayList<ServiceRequest> srList);
    }
}


