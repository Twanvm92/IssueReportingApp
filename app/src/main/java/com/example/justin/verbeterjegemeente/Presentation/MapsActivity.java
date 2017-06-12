package com.example.justin.verbeterjegemeente.Presentation;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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

/**
 * De gebruiker moet hier een locatie kiezen die bij het probleem hoort
 * De gekozen locatie wordt aangegeven met een rode marker op de map
 */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;
    private Marker marker;
    private Location currentLocation;
    private GoogleApiClient mApiClient;
    private LatLng currentLatLng;
    private Float zoom;

    /**
     * Savebutton geeft de LatLng mee aan MeldingActivity
     * Gps button zoekt de locatie van de gebruiker
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Intent in = getIntent();
        if (in.hasExtra("long")) {
            double lon = in.getDoubleExtra("long", 1);
            double lat = in.getDoubleExtra("lat", 1);
            zoom = in.getFloatExtra("zoom", 16.0f);
            currentLatLng = new LatLng(lat, lon);

        }


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.activityMaps_map_MapFragment);
        mapFragment.getMapAsync(this);

        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();

        FloatingActionButton saveButton = (FloatingActionButton) findViewById(R.id.activityMaps_fbtn_maps_Savebtn);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                if (marker != null) {
                    LatLng markerPosition = marker.getPosition();
                    i.putExtra("long", markerPosition.longitude); //post longitude
                    i.putExtra("lat", markerPosition.latitude); //post latitude
                    i.putExtra("zoom", mMap.getCameraPosition().zoom);
                }

                setResult(RESULT_OK, i); //set result and return
                finish();
            }
        });

        FloatingActionButton gpsButton = (FloatingActionButton) findViewById(R.id.activityMain_Fbtn_gps);
        gpsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reqFindLocation();
            }
        });
    }

    /**
     * Nadat de map geladen is kan door te drukken op de map een marker worden toegevoegd
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

    /**
     * Als de gebruiker de locatie wilt wijzigen, de vorige locatie als marker tonen
     */
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

    /**
     * Na opstarten camera bewegen naar default locatie tenzij de gebruiker een locatie heeft gekozen in fragment 1
     */
    public void getLocation() {
        if (currentLatLng == null) {
            CameraUpdate center = CameraUpdateFactory.newLatLngZoom(new LatLng(Constants.DEFAULT_LAT, Constants.DEFAULT_LONG), 12.0f);
            mMap.moveCamera(center); //update camera

        } else {
            CameraUpdate center = CameraUpdateFactory.newLatLngZoom(currentLatLng, zoom);
            mMap.moveCamera(center); //update camera
        }
    }

    /**
     * Ervoor zorgen dat teruggaan door middel van back press de marker alsnog meegegeven wordt
     */
    public void onBackPressed() {
        Intent i = new Intent();
        if (marker != null) {
            LatLng markerPosition = marker.getPosition();
            i.putExtra("long", markerPosition.longitude); //post longitude
            i.putExtra("lat", markerPosition.latitude); //post latitude
        }
        i.putExtra("zoom", mMap.getCameraPosition().zoom);
        setResult(RESULT_OK, i); //set result and return
        finish();
    }

    /**
     * Permissie wordt gevraagd als dit niet al gegeven is
     */
    public void reqFindLocation() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    Constants.MY_PERMISSIONS_LOCATION);

        } else {
            getUserLocation();
        }
    }

    /**
     * Locatie van de gebuiker ophalen
     */
    public void getUserLocation() {
        LocationRequest locationRequest = LocationRequest.create();
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
                final LocationSettingsStates states = LSresult.getLocationSettingsStates();
                switch (status.getStatusCode()) {
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
                        try {
                            status.startResolutionForResult(MapsActivity.this, Constants.REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {

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
