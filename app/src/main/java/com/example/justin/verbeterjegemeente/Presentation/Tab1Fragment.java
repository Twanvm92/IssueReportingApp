package com.example.justin.verbeterjegemeente.Presentation;


import android.widget.Button;

import com.example.justin.verbeterjegemeente.Business.MarkerHandler;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import br.com.bloder.magic.view.MagicButton;

/**
 * Created by Justin on 27-4-2017.
 */

public class Tab1Fragment extends SupportMapFragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    private Marker marker;
    private Button button;
    private MarkerHandler mHandler;
    private MagicButton btnTEST;

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
        mMap.setPadding(150,150,300,300);
        

        //locatie voorziening



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






}

