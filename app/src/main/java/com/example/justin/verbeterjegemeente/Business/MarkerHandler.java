package com.example.justin.verbeterjegemeente.Business;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

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
                for (Marker m : markerCategory) {
                    m.setVisible(true);
                }
                break;
        }
    }

    //initialize data and markers
    public void init() {

    }

    //get Data from api and make markers
    public void getData() {

    }

}
