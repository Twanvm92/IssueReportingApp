package com.example.justin.verbeterjegemeente;


import android.Manifest;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.location.Location;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import br.com.bloder.magic.view.MagicButton;

/**
 * Created by Justin on 27-4-2017.
 */

public class Tab1Fragment extends SupportMapFragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {
    private GoogleMap mMap;
    private Marker marker;
    private Button button;
    private MarkerHandler mHandler;
    private MagicButton btnTEST;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2;
    public Location currentLocation;
    private GoogleApiClient mApiClient;
    public Marker currentMarker;



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
        mApiClient = new GoogleApiClient.Builder(this.getContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();

    }

    public void onStart() {
        super.onStart();

        mApiClient.connect();
    }

    public void onStop() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mApiClient, this);

        if (mApiClient != null) {
            mApiClient.disconnect();
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

        setUpMap();
    }


    //set up map
    private void setUpMap() {
        //setup map settings
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setPadding(60, 100, 0, 180);


        //locatie voorziening
        initLocation();


        //markerHandler stuff
        mHandler = new MarkerHandler(mMap);
        mHandler.init();
        mHandler.setVisible("category");

        //click to add marker
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                //remove marker if exists
                if (marker != null)
                    marker.remove();

                //make new marker
                marker = mMap.addMarker(new MarkerOptions().position(latLng).title("marker")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

            }
        });
    }

    //locatie voorziening
    private void initLocation() {
        if (ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {

            } else {
                ActivityCompat.requestPermissions(this.getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                } else {

                }
                return;
            }
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getLocation();
    }

    public void getLocation() {
        if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if(currentMarker != null)
            currentMarker.remove();

        currentLocation = LocationServices.FusedLocationApi.getLastLocation(mApiClient);
        if(currentLocation != null) {
            LatLng currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            currentMarker = mMap.addMarker(new MarkerOptions().position(currentLatLng)
                    .title("Huidige locatie").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).visible(true)
            );
            CameraUpdate center = CameraUpdateFactory.newLatLngZoom(currentLatLng, 16.0f);
            mMap.moveCamera(center);
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
        currentLocation.setLatitude(location.getLatitude());
        currentLocation.setLongitude(location.getLongitude());

        getLocation();
    }
}

