package com.example.justin.verbeterjegemeente.Presentation;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.justin.verbeterjegemeente.Business.LocationSelectedListener;
import com.example.justin.verbeterjegemeente.Constants;
import com.example.justin.verbeterjegemeente.R;
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

import static com.example.justin.verbeterjegemeente.Constants.DEFAULT_LAT;
import static com.example.justin.verbeterjegemeente.Constants.DEFAULT_LONG;

/**
 * MapsActivity
 * Laat een map zien met daarop de huidige locatie. door op de kaart te klikken wordt de opgegeven locatie aangepast.
 * met de knop rechtsonderin keer je terug naar het meldingscherm en wordt de locatie meegegeven.
 */


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener{

    private GoogleMap mMap;
    private Marker marker;
    private Location currentLocation;
    private GoogleApiClient  mApiClient;
    private FloatingActionButton saveButton, gpsButton;
    private LatLng currentLatLng;
    private double lat, lon;

    /**
     * onCreate wordt opgeroepen wanneer de klasse wordt gemaakt. hierbij wordt de map opgeroepen,
     * een GoogleApiCLient aangemaakt en de savebutton gemaakt.
     *
     * de savebutton linked terug naar de meldingAcitivity en geeft de longitude en latitude mee.
     *
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Intent in = getIntent();
        if(in.hasExtra("long")) {
            lon = in.getDoubleExtra("long", 1);
            lat = in.getDoubleExtra("lat", 1);
            currentLatLng = new LatLng(lat, lon);

        }


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.activityMaps_map_MapFragment);
        mapFragment.getMapAsync(this);

        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();

        saveButton = (FloatingActionButton) findViewById(R.id.activityMaps_fbtn_maps_Savebtn);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                if (marker != null){
                    LatLng markerPosition = marker.getPosition();
                    i.putExtra("long", markerPosition.longitude); //post longitude
                    i.putExtra("lat", markerPosition.latitude); //post latitude
                }

                setResult(RESULT_OK, i); //set result and return
                finish();
            }
        });

        gpsButton = (FloatingActionButton) findViewById(R.id.activityMain_Fbtn_gps);
        gpsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reqFindLocation();
            }
        });
    }

    /**
     * onMapReady wordt opgeroepen wanneer de map geladen is. dan wordt er ook een onclicklistener aan de
     * kaart toegevoegd die de marker verplaatst op de kaart.
     *
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);


        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                //remove marker if exists

                if (marker != null)
                    marker.remove();
                currentLatLng = latLng;
                //make new marker
                marker = mMap.addMarker(new MarkerOptions().position(currentLatLng).title("marker")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

            }
        });
    }

    public void onStart() {
        super.onStart();
        mApiClient.connect();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (getIntent().hasExtra("marker")) {
            marker = mMap.addMarker(new MarkerOptions().position(currentLatLng)
                    .title("current location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).visible(true)
            );
        }
        getLocation();

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

    public void getLocation() {
        if (currentLatLng == null){
            CameraUpdate center = CameraUpdateFactory.newLatLngZoom(new LatLng(Constants.DEFAULT_LAT, Constants.DEFAULT_LONG), 12.0f);
            mMap.moveCamera(center); //update camera

        } else {
            CameraUpdate center = CameraUpdateFactory.newLatLngZoom(currentLatLng, 16.0f);
            mMap.moveCamera(center); //update camera
        }
    }

    public void onBackPressed() {
        Intent i = new Intent();
        if (marker != null){
            LatLng markerPosition = marker.getPosition();
            i.putExtra("long", markerPosition.longitude); //post longitude
            i.putExtra("lat", markerPosition.latitude); //post latitude
        }
        setResult(RESULT_OK, i); //set result and return
        finish();
    }

    public void reqFindLocation() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        Constants.MY_PERMISSIONS_LOCATION);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        Constants.MY_PERMISSIONS_LOCATION);
            }
        } else {
            getUserLocation();
        }
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
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            currentLocation = LocationServices.FusedLocationApi.getLastLocation(mApiClient);
                            if (currentLocation != null) {
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
                            status.startResolutionForResult(MapsActivity.this, Constants.REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e){

                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
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
                    getUserLocation();
                } else {
                    Toast.makeText(getApplicationContext(), "Kan locatie niet ophalen zonder permissie", Toast.LENGTH_SHORT).show();
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
                Log.i("onActivityResult", "Gps aanvraag afgewezen");
            }
        }
    }
}
