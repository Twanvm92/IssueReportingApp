package com.example.justin.verbeterjegemeente.Presentation;


import android.content.Intent;
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
import android.widget.Button;
import android.widget.Toast;

import com.example.justin.verbeterjegemeente.API.ConnectionChecker;
import com.example.justin.verbeterjegemeente.API.ServiceClient;
import com.example.justin.verbeterjegemeente.API.ServiceGenerator;
import com.example.justin.verbeterjegemeente.Business.BitmapGenerator;
import com.example.justin.verbeterjegemeente.Business.LocationSelectedListener;
import com.example.justin.verbeterjegemeente.Business.MarkerHandler;
import com.example.justin.verbeterjegemeente.Constants;
import com.example.justin.verbeterjegemeente.R;
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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;


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
    public GoogleMap mMap;
    private ArrayList<Marker> markerList;
    private Marker marker;
    private Button button;
    private MarkerHandler mHandler;
    private Location currentLocation;
    public LatLng currentLatLng;
    public GoogleApiClient mApiClient;
    public Marker currentMarker;

    ServiceClient client;
    private LocationSelectedListener locCallback;


    /*@Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab1_fragment,container,false);
        btnTEST = (MagicButton) view.findViewById(R.id.meldingmakenbutton);
        btnTEST.setMagicButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), MeldingActivity.class);
                startActivity(i);
            }

        });

        return view;
    }*/

    public void onCreate(Bundle savedInstaceState) {
        super.onCreate(savedInstaceState);

        client = ServiceGenerator.createService(ServiceClient.class);

        // create arraylist to contain created markers
        markerList = new ArrayList<Marker>();
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

        try {
            if(ConnectionChecker.isConnected()) {
                Call<ArrayList<ServiceRequest>> nearbyServiceRequests = client.getNearbyServiceRequests("" + DEFAULT_LONG, "" + DEFAULT_LAT, null, "300");
                nearbyServiceRequests.enqueue(new Callback<ArrayList<ServiceRequest>>() {
                    @Override
                    public void onResponse(Call<ArrayList<ServiceRequest>> call, Response<ArrayList<ServiceRequest>> response) {
                        if(response.isSuccessful()) {
                            ArrayList<ServiceRequest> srList = response.body();

                            for (ServiceRequest s : srList) {

                                mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(s.getLat(), s.getLong()))
                                        .title(s.getDescription())
                                        .icon(BitmapDescriptorFactory.fromBitmap(
                                                BitmapGenerator.getBitmapFromVectorDrawable(getContext(),
                                                        R.drawable.service_request_marker))));

                                Log.e("Opgehaalde servicereq: ", s.getDescription());
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
                        Toast.makeText(getContext(), getResources().getString(R.string.ePostRequest),
                                Toast.LENGTH_SHORT).show();
                        t.printStackTrace();
                    }
                });
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


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
                                Log.i("location1", currentLocation.getLatitude() + "");
                                Log.i("location2", currentLocation.getLongitude() + "");
                                currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                                CameraUpdate center = CameraUpdateFactory.newLatLngZoom(currentLatLng, 16.0f);
                                mMap.moveCamera(center);
                                if (locCallback != null) {
                                    locCallback.locationSelected(currentLatLng);
                                }
                            } else {
                                Log.e("getUserLocation", "Kan locatie niet ophalen");
                            }
                        } else {
                            Log.i("getUserLocation", "Geen toestemming");
                            Toast.makeText(getContext(), "Kan locatie niet ophalen", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try{
                            status.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
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
        currentLatLng = new LatLng(DEFAULT_LONG, DEFAULT_LAT);
        new AlertDialog.Builder(this.getContext())
                .setTitle("Locatie bepalen mislukt")
                .setMessage("het is niet gelukt uw huidige locatie te bepalen, mogelijk staat locatie voorziening uit, of is er geen internetverbinding. probeer het later opnieuw.")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).setIcon(android.R.drawable.ic_dialog_alert).show();

        CameraUpdate center = CameraUpdateFactory.newLatLngZoom(currentLatLng, 16.0f);
        mMap.moveCamera(center);

        if (locCallback != null) {
            locCallback.locationSelected(currentLatLng);
        }
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
        LatLng center = mMap.getCameraPosition().target;
        String camLat = "" + center.latitude;
        String camLng = "" + center.longitude;
        Log.e("Camera positie: ", "is veranderd");
//        Call<ArrayList<ServiceRequest>> nearbyServiceRequests = client.getNearbyServiceRequests(camLat, camLng, null, "300");
//        moet service_code meegeven...
        Call<ArrayList<ServiceRequest>> nearbyServiceRequests = client.getSimilarServiceRequests(camLat, camLng, null, "300", "OV");
        nearbyServiceRequests.enqueue(new Callback<ArrayList<ServiceRequest>>() {
            @Override
            public void onResponse(Call<ArrayList<ServiceRequest>> call, Response<ArrayList<ServiceRequest>> response) {
                if(response.isSuccessful()) {
                    ArrayList<ServiceRequest> srList = response.body();

                    for (ServiceRequest s : srList) {
                        mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(s.getLat(), s.getLong()))
                                .title(s.getDescription())
                                .icon(BitmapDescriptorFactory.fromBitmap(
                                        BitmapGenerator.getBitmapFromVectorDrawable(getContext(),
                                                R.drawable.service_request_marker)))
                        );
                        Log.e("Opgehaalde servicereq: ", s.getDescription() + "");
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

        if (locCallback != null) {
            locCallback.locationSelected(center);
        }

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
            case REQUEST_CHECK_SETTINGS:
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
}


