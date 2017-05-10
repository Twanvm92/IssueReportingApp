package com.example.justin.verbeterjegemeente;


import android.content.Intent;

import android.location.Location;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.common.api.GoogleApiClient;
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

public class Tab1Fragment extends SupportMapFragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    private Marker marker;
    private Button button;
    private MarkerHandler mHandler;
    private MagicButton btnTEST;

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
