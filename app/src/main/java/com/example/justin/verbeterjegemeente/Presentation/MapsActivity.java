package com.example.justin.verbeterjegemeente.Presentation;

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
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.example.justin.verbeterjegemeente.R;
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

/**
 * MapsActivity
 * Laat een map zien met daarop de huidige locatie. door op de kaart te klikken wordt de opgegeven locatie aangepast.
 * met de knop rechtsonderin keer je terug naar het meldingscherm en wordt de locatie meegegeven.
 */


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;
    private Marker marker;
    private Location currentLocation;
    private GoogleApiClient mApiClient;
    private FloatingActionButton saveButton;

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
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //maak apiclient
        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();

        //maak saveButton
        saveButton = (FloatingActionButton) findViewById(R.id.maps_saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.putExtra("long", currentLocation.getLongitude()); //post longitude
                i.putExtra("lat", currentLocation.getLatitude()); //post latitude
                Log.i("before Long: ", String.valueOf(currentLocation.getLongitude())); //log values
                Log.i("before Lat: ", String.valueOf(currentLocation.getLatitude()));
                setResult(RESULT_OK, i); //set result and return
                finish();
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
        getLocation();
    }

    /**
     * getLocation checkt of er toestemming is gegeven.
     * vervolgens wordt de locatie opgehaaldt en in een variabele gezet
     * aan de hand van die variabele wordt de marker op de map gezet.
     */

    public void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if(marker != null)//verwijder de oude marker
            marker.remove();

        currentLocation = LocationServices.FusedLocationApi.getLastLocation(mApiClient); //haal locatie op

        //Toast.makeText(this, "Long: " + currentLocation.getLongitude() + " Lat: " + currentLocation.getLatitude(),Toast.LENGTH_SHORT).show();

        if(currentLocation != null) {
            LatLng currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            marker = mMap.addMarker(new MarkerOptions().position(currentLatLng)
                    .title("current location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).visible(true)
            ); //maak nieuwe marker
            CameraUpdate center = CameraUpdateFactory.newLatLngZoom(currentLatLng, 16.0f);
            mMap.moveCamera(center); //update camera
        }
    }

    public void onBackPressed() {
        Intent i = new Intent();
        i.putExtra("long", currentLocation.getLongitude()); //post longitude
        i.putExtra("lat", currentLocation.getLatitude()); //post latitude
        Log.i("before Long: ", String.valueOf(currentLocation.getLongitude())); //log values
        Log.i("before Lat: ", String.valueOf(currentLocation.getLatitude()));
        setResult(RESULT_OK, i); //set result and return
        finish();
    }
}
