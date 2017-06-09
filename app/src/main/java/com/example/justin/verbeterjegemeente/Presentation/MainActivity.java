package com.example.justin.verbeterjegemeente.Presentation;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.justin.verbeterjegemeente.*;
import com.example.justin.verbeterjegemeente.Adapters.SectionsPageAdapter;
import com.example.justin.verbeterjegemeente.Business.LocationSelectedListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import static com.example.justin.verbeterjegemeente.Constants.DEFAULT_LAT;
import static com.example.justin.verbeterjegemeente.Constants.DEFAULT_LONG;
import static com.example.justin.verbeterjegemeente.Constants.MY_PERMISSIONS_LOCATION;
import static com.example.justin.verbeterjegemeente.Constants.REQUEST_CHECK_SETTINGS;


public class MainActivity extends AppCompatActivity implements LocationSelectedListener {

    private static final String TAG = "MainActivity";

    private SectionsPageAdapter mSectionsPageAdapter;

    private ViewPager mViewPager;
    private FloatingActionButton fab;
    private FloatingActionButton gps;
    private Tab1Fragment tabFragment = new Tab1Fragment();
    private LatLng currentLatLng;
    boolean popupShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: Starting.");

        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        gps = (FloatingActionButton) findViewById(R.id.activityMain_Fbtn_gps);
        gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabFragment.reqFindLocation();
            }
        });

        fab = (FloatingActionButton) findViewById(R.id.activityMain_Fbtn_FAB);
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
                        gps.show();
                        break;

                    case 1:
                        fab.show();
                        gps.hide();
                        break;

                    case 2:
                        fab.hide();
                        gps.hide();
                        break;

                    default:
                        fab.hide();
                        gps.hide();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Constants.MY_PERMISSIONS_LOCATION:
                tabFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    public void getLocation() {
        Location currentLocation = null;
        GoogleMap mMap = tabFragment.mMap;
        GoogleApiClient mApiClient = tabFragment.mApiClient;

//            if (mApiClient != null) {
//
//                // commented for testing purposed. Now jumps to default lat & long in Helsinki.
//                // uncomment this line
//                // currentLocation = LocationServices.FusedLocationApi.getLastLocation(mApiClient);
//
//                if (currentLocation != null) {
//                    currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
//                } else {
//                    currentLatLng = new LatLng(DEFAULT_LONG, DEFAULT_LAT);
//
//                }
//                CameraUpdate center = CameraUpdateFactory.newLatLngZoom(currentLatLng, 16.0f);
//                mMap.moveCamera(center);
//            }
//                else {
//                currentLatLng = new LatLng(DEFAULT_LONG, DEFAULT_LAT);
//                CameraUpdate center = CameraUpdateFactory.newLatLngZoom(currentLatLng, 16.0f);
//                mMap.moveCamera(center);
//
//            }




    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent();
        setResult(RESULT_CANCELED, i);
        super.onBackPressed();
    }

    @Override
    public void locationSelected(LatLng curLatLong) {
        // The user selected the headline of an article from the HeadlinesFragment
        // Do something here to display that article

        Tab2Fragment tab2Frag = (Tab2Fragment)
                getSupportFragmentManager().getFragments().get(1);

        if (tab2Frag != null) {
            // If article frag is available, we're in two-pane layout...

            // Call a method in the ArticleFragment to update its content
            Log.e("MainActivity: ", "We are in a two pane layout..");

            tab2Frag.updateCurrentLoc(curLatLong);
        } else {
            // Otherwise, we're in the one-pane layout and must swap frags...

            // Create fragment and give it an argument for the selected article
            Tab2Fragment newFragment = new Tab2Fragment();
            Bundle args = new Bundle();
            args.putDouble("CURRENT_LAT",curLatLong.latitude);
            args.putDouble("CURRENT_LONG",curLatLong.longitude);
            newFragment.setArguments(args);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            transaction.replace(R.id.tab2_fragment_layout, newFragment);
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                tabFragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}
