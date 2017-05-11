package com.example.justin.verbeterjegemeente;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;
    private Marker marker;
    private Location currentLocation;
    private GoogleApiClient mApiClient;
    private FloatingActionButton saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        saveButton = (FloatingActionButton) findViewById(R.id.maps_saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.putExtra("long", currentLocation.getLongitude());
                i.putExtra("lat", currentLocation.getLatitude());
                Log.i("Long: ", String.valueOf(currentLocation.getLongitude()));
                Log.i("Lat: ", String.valueOf(currentLocation.getLatitude()));
                setResult(RESULT_OK, i);
                finish();
            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                //remove marker if exists
                if (marker != null)
                    marker.remove();
                currentLocation.setLongitude(latLng.longitude);
                currentLocation.setLatitude(latLng.latitude);
                //make new marker
                marker = mMap.addMarker(new MarkerOptions().position(latLng).title("marker")
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if(marker != null)
            marker.remove();

        currentLocation = LocationServices.FusedLocationApi.getLastLocation(mApiClient);
        Toast.makeText(this, "Long: " + currentLocation.getLongitude() + " Lat: " + currentLocation.getLatitude(),Toast.LENGTH_SHORT).show();
        if(currentLocation != null) {
            LatLng currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            marker = mMap.addMarker(new MarkerOptions().position(currentLatLng)
                    .title("current location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).visible(true)
            );
            CameraUpdate center = CameraUpdateFactory.newLatLng(currentLatLng);
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);

            mMap.moveCamera(center);
            mMap.animateCamera(zoom);
        }
    }
}
