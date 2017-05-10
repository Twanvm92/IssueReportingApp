package com.example.justin.verbeterjegemeente;

import android.location.Location;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MarkerHandler {

    private GoogleMap mMap;
    private ArrayList<Marker> markerCategory = new ArrayList<>();
    private GoogleApiClient mApiClient;
    private LocationRequest mLocationRequest;

    //constructor
    public MarkerHandler(GoogleMap map) {
        mMap = map;
    }

    //set markers for specific category visible
    public void setVisible(String category) {
        switch (category) {
            case "category":
                for(Marker m : markerCategory) {
                    m.setVisible(true);
                }
                break;
        }
    }

    //initialize data and markers
    public void init() {

        //test data
        Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(10, 10)).visible(false).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        markerCategory.add(marker);
    }

    //get Data from api and make markers
    public void getData() {

    }

}
