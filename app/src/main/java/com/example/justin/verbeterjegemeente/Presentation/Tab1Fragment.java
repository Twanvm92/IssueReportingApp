package com.example.justin.verbeterjegemeente.Presentation;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import com.example.justin.verbeterjegemeente.API.ConnectionChecker;
import com.example.justin.verbeterjegemeente.API.ServiceClient;
import com.example.justin.verbeterjegemeente.API.ServiceGenerator;
import com.example.justin.verbeterjegemeente.Business.BitmapGenerator;
import com.example.justin.verbeterjegemeente.Business.LocationSelectedListener;
import com.example.justin.verbeterjegemeente.Business.MarkerHandler;
import com.example.justin.verbeterjegemeente.Constants;
import com.example.justin.verbeterjegemeente.R;
import com.example.justin.verbeterjegemeente.domain.Service;
import com.example.justin.verbeterjegemeente.domain.ServiceRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import static com.example.justin.verbeterjegemeente.Constants.DEFAULT_LAT;
import static com.example.justin.verbeterjegemeente.Constants.DEFAULT_LONG;
import static com.example.justin.verbeterjegemeente.Constants.REQUEST_CHECK_SETTINGS;

/**
 * Created by Justin on 27-4-2017.
 */

public class Tab1Fragment extends SupportMapFragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener, GoogleMap.OnCameraIdleListener {
    public GoogleMap mMap;;
    private MarkerHandler mHandler;
    private Location currentLocation;
    public LatLng currentLatLng;
    public GoogleApiClient mApiClient;
    ServiceClient client;
    private LocationSelectedListener locCallback;
    private List<Service> serviceList;
    ArrayAdapter<String> catagoryAdapter;
    private ArrayList<String> catagoryList;
    private Spinner catagorySpinner;
    private String currentRadius;
    private String servCodeQ;
    private boolean eersteKeer = true;

    public void onCreate(Bundle savedInstaceState) {
        super.onCreate(savedInstaceState);

        // get user selected radius and cat or use default radius and cat
        SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        int rValue = prefs.getInt(getString(R.string.activityMain_saved_radius), 20); // 20 is default
        currentRadius = Integer.toString(rValue);
        String savedservCodeQ = prefs.getString(getString(R.string.activityMain_saved_servcodeQ),
                getString(R.string.geenFilter));

        // check if service code is not default value
        // otherwise make String null
        // this will let API requests not take in account service codes
        if(savedservCodeQ.equals("")) {
            servCodeQ = null;
        } else {
            servCodeQ = savedservCodeQ;
        }

        client = ServiceGenerator.createService(ServiceClient.class);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            locCallback = (LocationSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }

    }

    public void onStart() {
        super.onStart();
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
                            }
                        }).setIcon(android.R.drawable.ic_dialog_alert).show();
            }
        } catch (Exception e) {
            Log.i("Exception: ", e.getLocalizedMessage());
        }

    }

    public void onStop() {
        try {
            if (!ConnectionChecker.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mApiClient, this);
                if (mApiClient != null) {
                    mApiClient.disconnect();

                }
            }
        } catch(Exception e) {
            Log.i("EXCEPTION: ", e.getLocalizedMessage());
        }

        super.onStop();
    }



    //set up map on resume
    public void onResume() {
        super.onResume();

        setUpMapIfNeeded();
    }

    //load map if needed
    private void setUpMapIfNeeded() {
        if (mMap == null) {
            getMapAsync(this);
        }
    }

    //set up map when map is loaded
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnCameraIdleListener(this);


        //get Current radius selected by user in MainActivity
        System.out.println("Current radius: " + currentRadius);

        setUpMap();
        Log.e("MAP: ", "map is klaargezet");
    }


    //set up map
    private void setUpMap() {
        //setup map settings
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setPadding(60, 100, 0, 180);

//        setup Google Api
        initApi();

        //markerHandler stuff
        mHandler = new MarkerHandler(mMap);
        mHandler.init();
        mHandler.setVisible("category");
    }

    public void initApi() {
        mApiClient = new GoogleApiClient.Builder(this.getContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        mApiClient.connect();

        reqFindLocation();
    }

    public void getUserLocation(){
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        builder.setAlwaysShow(true);

        final PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>(){

            @Override
            public void onResult(@NonNull LocationSettingsResult LSresult) {
                final Status status = LSresult.getStatus();
                final LocationSettingsStates states = LSresult.getLocationSettingsStates();
                switch (status.getStatusCode()){
                    case LocationSettingsStatusCodes.SUCCESS:
                        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            currentLocation = LocationServices.FusedLocationApi.getLastLocation(mApiClient);
                            if (currentLocation != null) {
                                eersteKeer = false;
                                currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                                CameraUpdate center = CameraUpdateFactory.newLatLngZoom(currentLatLng, 16.0f);
                                mMap.moveCamera(center);
                            } else {
                                Log.e("getUserLocation", "Kan locatie niet ophalen");
                            }
                        } else {
                            Log.e("getUserLocation", "Geen toestemming");
                        }
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try{
                            status.startResolutionForResult(getActivity(), Constants.REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e){

                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:

                        break;
                }
            }
        });
    }

    //locatie voorziening
    private void initLocation() {

    }




    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    public void getLocation() {
        currentLatLng = new LatLng(DEFAULT_LAT, DEFAULT_LONG);
        new AlertDialog.Builder(this.getContext())
                .setTitle("Locatie bepalen mislukt")
                .setMessage("het is niet gelukt uw huidige locatie te bepalen, mogelijk staat locatie voorziening uit, of is er geen internetverbinding. probeer het later opnieuw.")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).setIcon(android.R.drawable.ic_dialog_alert).show();

        CameraUpdate center = CameraUpdateFactory.newLatLngZoom(currentLatLng, 12.0f);
        mMap.moveCamera(center);
        eersteKeer = false;
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }


    @Override
    public void onCameraIdle() {
        //get Current radius selected by user in MainActivity
        System.out.println("Current radius: " + currentRadius);

        LatLng center = mMap.getCameraPosition().target;
        String camLat = "" + center.latitude;
        String camLng = "" + center.longitude;

        if (!eersteKeer) {
            if (Double.compare(center.latitude, Constants.DEFAULT_LAT) != 0 || Double.compare(center.longitude, Constants.DEFAULT_LONG) != 0) {
                currentLatLng = new LatLng(center.latitude, center.longitude);
                if (locCallback != null) {
                    locCallback.locationSelected(currentLatLng);
                }
            }
        }


        Log.e("Camera positie: ", "is veranderd");

        // commented this line for testing getting service request based on radius from Helsinki Live API
//        Call<ArrayList<ServiceRequest>> nearbyServiceRequests = client.getNearbyServiceRequests(
//                  camLat, camLng, null, currentRadius, "OV");
        Call<ArrayList<ServiceRequest>> nearbyServiceRequests;
        if (servCodeQ == null) {
            Log.e("oncameraidle sercoeQ: ", "" +servCodeQ);
            nearbyServiceRequests = client.getNearbyServiceRequests(
                    camLat, camLng, null, currentRadius);
        } else {
            Log.e("oncameraidle sercoeQ: ", "" +servCodeQ);
            nearbyServiceRequests = client.getNearbyServiceRequests(
                    camLat, camLng, null, currentRadius, servCodeQ);
        }
        
        nearbyServiceRequests.enqueue(new Callback<ArrayList<ServiceRequest>>() {
            @Override
            public void onResponse(Call<ArrayList<ServiceRequest>> call, Response<ArrayList<ServiceRequest>> response) {
                if(response.isSuccessful()) {
                    ArrayList<ServiceRequest> srList = response.body();

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

                        Log.e("Opgehaalde servicereq: ", s.getServiceCode() + "");

                    }

                } else {
                    try { //something went wrong. Show the user what went wrong
                        JSONArray jObjErrorArray = new JSONArray(response.errorBody().string());
                        JSONObject jObjError = (JSONObject) jObjErrorArray.get(0);

                        Toast.makeText(getContext(), jObjError.getString("description"),
                                Toast.LENGTH_SHORT).show();
                        Log.i("Error message: ", jObjError.getString("description"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

        @Override
        public void onFailure(Call<ArrayList<ServiceRequest>> call, Throwable t) {
            Toast.makeText(getContext(), t.getMessage().toString(),
                    Toast.LENGTH_SHORT).show();
            t.printStackTrace();
        }
        });
    }

    public void reqFindLocation() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this.getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        Constants.MY_PERMISSIONS_LOCATION);
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        Constants.MY_PERMISSIONS_LOCATION);
            }
        } else {
            getUserLocation();
        }
    }

    /**
     * Controleren of permissies goed gekeurd zijn door de gebruiker
     * @param requestCode meegegeven activiteit nummer die gedaan is
     * @param permissions permissies die aangevraagd worden
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
                    getUserLocation();
                } else {
                    Log.i("onRequestPermResult", "Geen toestemming gekregen, eerste keer dat map geladen wordt default lat/long gepakt");
                    if (currentLatLng == null){
                        getLocation();
                    } else {
                        Log.i("onRequestPermResult", "Geen toestemming gekregen");
                        Toast.makeText(getContext(), "Kan locatie niet ophalen zonder permissie", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.REQUEST_CHECK_SETTINGS:
                if (resultCode == Activity.RESULT_OK) {
                    reqFindLocation();
                } else {
                    if (currentLatLng == null) {
                        getLocation();
                    }
                    Log.i("onActivityResult", "Gps aanvraag afgewezen");
                }
        }
    }

    /**
     * This method will update the radius and service codes connected to the category
     * set by the user. After that it will get new service requests based on the new radius and
     * category filter and add them as markers on a Google map
     * @param radius radius in meters
     * @param servCodeQ String with service codes appending by a , delimiter
     *                  that can be used for filtering service requests.
     */
    public void updateRadiusCat(int radius, String servCodeQ) {
        String pRadius = (String) Integer.toString(radius);
        currentRadius = pRadius;
        this.servCodeQ = servCodeQ;
        String currentLat;
        String currentLng;
        if (currentLatLng == null){
            currentLat = Double.toString(Constants.DEFAULT_LAT);
            currentLng = Double.toString(Constants.DEFAULT_LONG);
        } else {
            currentLat = Double.toString(currentLatLng.latitude);
            currentLng = Double.toString(currentLatLng.longitude);
            Log.e("Radius update tab1: ", currentRadius);
        }

        Call<ArrayList<ServiceRequest>> nearbyServiceRequests;
        if(servCodeQ != null) {
            nearbyServiceRequests = client.getNearbyServiceRequests(
                    currentLat, currentLng, null, currentRadius, servCodeQ);
            Log.e("servCodeq update tab1: ", servCodeQ);
        } else {
            Log.e("servCodeq update tab1: ", "null");
            nearbyServiceRequests = client.getNearbyServiceRequests(
                    currentLat, currentLng, null, currentRadius);
        }

        nearbyServiceRequests.enqueue(new Callback<ArrayList<ServiceRequest>>() {
            @Override
            public void onResponse(Call<ArrayList<ServiceRequest>> call, Response<ArrayList<ServiceRequest>> response) {
                if(response.isSuccessful()) {
                    ArrayList<ServiceRequest> srList = response.body();

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
                        Log.e("Opgehaalde serv: ", s.getServiceCode() + "");
                    }

                } else {
                    try { //something went wrong. Show the user what went wrong
                        JSONArray jObjErrorArray = new JSONArray(response.errorBody().string());
                        JSONObject jObjError = (JSONObject) jObjErrorArray.get(0);

                        Toast.makeText(getContext(), jObjError.getString("description"),
                                Toast.LENGTH_SHORT).show();
                        Log.i("Error message: ", jObjError.getString("description"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<ArrayList<ServiceRequest>> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage().toString(),
                        Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });

    }
}


