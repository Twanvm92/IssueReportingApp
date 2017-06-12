package com.example.justin.verbeterjegemeente.Presentation;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.justin.verbeterjegemeente.API.RequestManager;
import com.example.justin.verbeterjegemeente.API.ServiceClient;
import com.example.justin.verbeterjegemeente.Adapters.SectionsPageAdapter;
import com.example.justin.verbeterjegemeente.Business.LocationSelectedListener;
import com.example.justin.verbeterjegemeente.Business.ServiceManager;
import com.example.justin.verbeterjegemeente.Constants;
import com.example.justin.verbeterjegemeente.R;
import com.example.justin.verbeterjegemeente.domain.Service;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;


public class MainActivity extends AppCompatActivity implements LocationSelectedListener, RequestManager.OnServicesReady {

    private static final String TAG = "MainActivity";
    private SectionsPageAdapter mSectionsPageAdapter;
    private int rValue;
    private ViewPager mViewPager;
    private FloatingActionButton fab;
    private FloatingActionButton gps;
    private Tab1Fragment tabFragment = new Tab1Fragment();
    private Tab2Fragment tab2Fragment = new Tab2Fragment();
    private LatLng currentLatLng;
    private List<Service> serviceList;
    ArrayAdapter<String> catagoryAdapter;
    private ArrayList<String> catagoryList;
    private Spinner catagorySpinner;
    private ServiceClient client;
    private Locale myLocale;
    private RequestManager reqManager;
    private String servCodeQ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: Starting.");

        // from here all the API requests will be handled
        reqManager = new RequestManager(this);
        // set callback for data passing
        reqManager.setOnServicesReadyCallb(this);
        // launch Retrofit callback and retrieve services asynchronously
        reqManager.getServices();

        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        // create an arraylist that will contain different categories fetched from an open311 interface
        catagoryList = new ArrayList<String>();
        catagoryList.add(getString(R.string.geenFilter));
        catagoryAdapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_spinner_item, catagoryList);

        // Floating action button with expandable menu on tab1
        final FabSpeedDial fabSpeedDial = (FabSpeedDial) findViewById(R.id.activityMain_Fbtn_speeddial);
        fabSpeedDial.setMenuListener(new SimpleMenuListenerAdapter() {
            @Override

            public boolean onMenuItemSelected(MenuItem menuItem) {


                switch (menuItem.getItemId()) {
                    case R.id.activityMain_item_filters:

                        //create a new custom dialog
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                        View mView = getLayoutInflater().inflate(R.layout.activity_main_filters_dialog, null);

                        final TextView tvRadiusD = (TextView) mView.findViewById(R.id.filterdialog_tv_afstand6);
                        final SeekBar sbRadius = (SeekBar) mView.findViewById(R.id.filterdialog_sb_radius);

                        // get user selected radius or use default radius
                        SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
                        rValue = prefs.getInt(getString(R.string.activityMain_saved_radius), 20); // 20 is default
                        String savedCat = prefs.getString(getString(R.string.activityMain_saved_category),
                                getString(R.string.geenFilter));

                        // set all values for the seekbar
                        sbRadius.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
                        sbRadius.getThumb().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
                        sbRadius.setMax(300);
                        sbRadius.incrementProgressBy(2);
                        sbRadius.setProgress(rValue);
                        tvRadiusD.setText(rValue + getString(R.string.radiusMeters));

                        sbRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                // show progress in a Textview
                                tvRadiusD.setText(String.valueOf(progress) + getString(R.string.radiusMeters));

                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {
                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                // put new radius value in preferences
                                SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
                                prefs.edit().putInt(getString(R.string.activityMain_saved_radius), sbRadius.getProgress()).apply();

                                rValue = sbRadius.getProgress();

                            }
                        });


                        catagorySpinner = (Spinner) mView.findViewById(R.id.filterdialog_sp_categorieen);
                        catagoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        catagorySpinner.setAdapter(catagoryAdapter);
                        int spinnerPosition = catagoryAdapter.getPosition(savedCat);
                        catagorySpinner.setSelection(spinnerPosition);


                        builder1.setView(mView);
                        AlertDialog dialog = builder1.create();


                        dialog.show();

                        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                // get the currently selected category
                                String currCatag = catagorySpinner.getSelectedItem().toString();
                                ArrayList<String> catCodeList = new ArrayList<String>();
                                SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);

                                // generate a string with appended service codes
                                // depending on what services are available and what category filter is
                                // currently active.
                                servCodeQ = ServiceManager.genServiceCodeQ(serviceList, currCatag);
                                // save the currently active category filter and the string of service codes
                                // that belong to that category in the users preferences
                                prefs.edit().putString(getString(R.string.activityMain_saved_category), currCatag).apply();
                                prefs.edit().putString(getString(R.string.activityMain_saved_servcodeQ), servCodeQ).apply();

                                // notify Tab1Fragment and Tab2Fragment that a new radius and category
                                // was selected
                                radiusCategSelected(rValue, servCodeQ);
                            }
                        });

                        break;
                    case R.id.activityMain_item_report:
//                        if (tabFragment.currentLatLng != null) {
//                            double longCor = tabFragment.currentLatLng.longitude;
//                            double latCor = tabFragment.currentLatLng.latitude;
//                            currentLatLng = new LatLng(longCor, latCor);
//                        }
//                        Intent in = new Intent(getApplicationContext(),
//                                com.example.justin.verbeterjegemeente.Presentation.MeldingActivity.class);
//                        if (currentLatLng != null) {
//                            in.putExtra("long", currentLatLng.longitude);
//                            in.putExtra("lat", currentLatLng.latitude);
//                        }
//                        startActivity(in);
                        Intent in = new Intent(getApplicationContext(),
                                com.example.justin.verbeterjegemeente.Presentation.MeldingActivity.class);
                        if (currentLatLng != null) {
                            in.putExtra("long", currentLatLng.longitude);
                            in.putExtra("lat", currentLatLng.latitude);
                        }
                        startActivity(in);
                        break;
                    case R.id.activityMain_item_gps:
                        tabFragment.reqFindLocation();
                        Toast.makeText(getApplicationContext(), "GPS knop is aangeklikt", Toast.LENGTH_SHORT).show();
                }

                return true;
            }
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        fabSpeedDial.show();
                        break;

                    case 1:
                        fabSpeedDial.show();
                        break;

                    case 2:
                        fabSpeedDial.hide();
                        break;

                    default:
                        fabSpeedDial.hide();
                        break;
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        tabLayout.getTabAt(0).setIcon(R.drawable.mapicon);
        tabLayout.getTabAt(1).setIcon(R.drawable.listicon);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_item) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            View mView = getLayoutInflater().inflate(R.layout.dialog, null);
            Button mMijnmeldingen = (Button) mView.findViewById(R.id.alertdialog_btn_mijnmeldingen);
            Button mInstellingen = (Button) mView.findViewById(R.id.alertdialog_btn_instellingen);
            Button mOver = (Button) mView.findViewById(R.id.alertdialog_btn_over);

            mMijnmeldingen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getApplicationContext(), FollowingActivity.class);
                    startActivity(i);
                }
            });

            mInstellingen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                    View mView = getLayoutInflater().inflate(R.layout.dialog2, null);
                    Button nederlands = (Button) mView.findViewById(R.id.alertdialog_btn_nederlands);
                    nederlands.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(getApplication(), "Welkom", Toast.LENGTH_SHORT).show();

                            setLocale("nl");
                            saveLocale("nl");
                            recreate();
                        }
                    });
                    Button engels = (Button) mView.findViewById(R.id.alertdialog_btn_engels);
                    engels.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(getApplication(), "Welcome", Toast.LENGTH_SHORT).show();

                            setLocale("en");
                            saveLocale("en");
                            recreate();

                        }
                    });

                    SeekBar radius = (SeekBar) mView.findViewById(R.id.alertdialog_sb_radius);
                    final TextView radius_afstand = (TextView) mView.findViewById(R.id.alertdialog_tv_afstand);
                    radius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            radius_afstand.setText(String.valueOf(progress) + "Km.");
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {
                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {
                        }
                    });

                    builder1.setView(mView);
                    AlertDialog dialog = builder1.create();
                    dialog.show();

                }
            });

            mOver.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Functie van knop
                }
            });

            builder.setView(mView);
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Changing language to user's choice
     *
     * @param lang language user is requesting
     */
    public void setLocale(String lang) {

        myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
//        Intent refresh = new Intent(this, MainActivity.class);
//        startActivity(refresh);

    }


    /**
     * Saving preferred language
     *
     * @param lang language user is requesting
     */
    public void saveLocale(String lang) {
        String langPref = "Language";
        SharedPreferences prefs = getApplication().getSharedPreferences("CommonPrefs",
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(langPref, lang);
        editor.commit();

//        refresh van huidige fragment werkt, backpress naar meldingen ook
//        andere activiteiten worden nog niet refresht
//        misschien is er een betere manier ipv elke activiteit apart op te vangen..
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());

        /*// get user selected radius and cat or use default radius and cat
        SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
        rValue = prefs.getInt(getString(R.string.activityMain_saved_radius), 20); // 20 is default
        String savedservCodeQ = prefs.getString(getString(R.string.activityMain_saved_servcodeQ),
                getString(R.string.geenFilter));

        // check if service code is not default value
        // otherwise make String null
        // this will let API requests not take in account service codes
        if(savedservCodeQ.equals("")) {
            savedservCodeQ = null;
        }

        //create bundle and put current saved radius and service code values in the bundle
        Bundle bundle = new Bundle();
        String sValue = Integer.toString(rValue);
        bundle.putString("RADIUS_VALUE",sValue);
        *//*bundle.putString("SERVICE_CODE_VALUE",savedservCodeQ);*/

        // pass values as a bundle to the Tab1Fragment
//        tabFragment.setArguments(bundle);
        adapter.addFragment(tabFragment, "");

        // pass values as a bundle to the Tab1Fragment
//        tab2Fragment.setArguments(bundle);
        adapter.addFragment(tab2Fragment, "");

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
        Tab2Fragment tab2Frag = (Tab2Fragment)
                getSupportFragmentManager().getFragments().get(1);

        if (tab2Frag != null) {
            // If tab1 frag is available, we're in two-pane layout...

            // Call a method in the Tab1Fragment to update its content
            Log.e("MainActivity: ", "We are in a two pane layout..");

            tab2Frag.updateCurrentLoc(curLatLong);
        } else {
            // Otherwise, we're in the one-pane layout and must swap frags...

            // Create fragment and give it an argument for the selected LatLng
            Tab2Fragment newFragment = new Tab2Fragment();
            Bundle args = new Bundle();
            args.putDouble("CURRENT_LAT", curLatLong.latitude);
            args.putDouble("CURRENT_LONG", curLatLong.longitude);
            newFragment.setArguments(args);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            // The user selected the headline of an article from the HeadlinesFragment
            // Do something here to display that article
        }
        currentLatLng = curLatLong;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.REQUEST_CHECK_SETTINGS:
                tabFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * This method will pass the radius that is selected by the user in a custom dialog
     * to the Tab1Fragment where the radius will be used to find service requests and
     * place them on the map and in the
     *
     * @param value     value of the progress of the radius seekbar in custom dialog
     * @param servCodeQ
     */
    public void radiusCategSelected(int value, String servCodeQ) {

        if (servCodeQ.equals("")) {
            servCodeQ = null;
        }

        Tab1Fragment tab1Fragment = null;

        if (getSupportFragmentManager().getFragments() != null) {
            tab1Fragment = (Tab1Fragment)
                    getSupportFragmentManager().getFragments().get(0);
        }

        if (tab1Fragment != null) {

            // update the radius and category selected in the Tab1Fragment
            tab1Fragment.updateRadiusCat(value, servCodeQ);
        }

        Tab2Fragment tab2Fragment = null;

        if (getSupportFragmentManager().getFragments() != null) {
            tab2Fragment = (Tab2Fragment)
                    getSupportFragmentManager().getFragments().get(1);
        }

        if (tab2Fragment != null) {

            // update the radius and category selected in the Tab2Fragment
            tab2Fragment.updateRadiusCat(value, servCodeQ);
        }
    }

    @Override
    public void servicesReady(List<Service> services) {
        serviceList = services;
        // update the catagoryList with main categories generated from the service list
        catagoryList = ServiceManager.genMainCategories(services, catagoryList);

        // let the adapter know that data has changed
        catagoryAdapter.notifyDataSetChanged();

    }
}
