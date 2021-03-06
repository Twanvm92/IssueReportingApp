package com.example.justin.verbeterjegemeente.Presentation;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.justin.verbeterjegemeente.*;
import com.example.justin.verbeterjegemeente.Adapters.SectionsPageAdapter;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private SectionsPageAdapter mSectionsPageAdapter;

    private ViewPager mViewPager;
    private FloatingActionButton fab;
    private Tab1Fragment tabFragment = new Tab1Fragment();
    private LatLng currentLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: Starting.");

        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tabFragment.currentLatLng != null) {
                    double longCor = tabFragment.currentLatLng.longitude;
                    double latCor = tabFragment.currentLatLng.latitude;
                    currentLatLng = new LatLng(longCor, latCor);
                }
                Intent in = new Intent(getApplicationContext(),
                        com.example.justin.verbeterjegemeente.Presentation.MeldingActivity.class);
                if(currentLatLng != null){
                    in.putExtra("long", currentLatLng.longitude);
                    in.putExtra("lat", currentLatLng.latitude);
                }
                startActivity(in);
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        fab.show();

                        break;

                    case 1:
                        fab.show();
                        break;

                    case 2:
                        fab.hide();
                        break;

                    default:
                        fab.hide();
                        break;
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        tabLayout.getTabAt(0).setIcon(R.drawable.mapicon);
        tabLayout.getTabAt(1).setIcon(R.drawable.listicon);
        tabLayout.getTabAt(2).setIcon(R.drawable.accounticon);

    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(tabFragment, "");
        adapter.addFragment(new Tab2Fragment(), "");
        adapter.addFragment(new Tab3Fragment(), "");
        viewPager.setAdapter(adapter);
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Location currentLocation;
        GoogleMap mMap = tabFragment.mMap;
        GoogleApiClient mApiClient = tabFragment.mApiClient;
        switch (requestCode) {
            case 2: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }

                    if (mApiClient != null) {
                        currentLocation = LocationServices.FusedLocationApi.getLastLocation(mApiClient);

                        if (currentLocation != null) {
                            currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                        } else {
                            currentLatLng = new LatLng(51.58656, 4.77596);
                        }
                        CameraUpdate center = CameraUpdateFactory.newLatLngZoom(currentLatLng, 16.0f);
                        mMap.moveCamera(center);
                    } else {
                        currentLatLng = new LatLng(51.58656, 4.77596);
                        CameraUpdate center = CameraUpdateFactory.newLatLngZoom(currentLatLng, 16.0f);
                        mMap.moveCamera(center);

                    }
                    return;
                }
            }


        }
    }
}
