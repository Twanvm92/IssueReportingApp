package com.example.justin.verbeterjegemeente.Presentation;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Location;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
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

import com.example.justin.verbeterjegemeente.*;
import com.example.justin.verbeterjegemeente.API.ConnectionChecker;
import com.example.justin.verbeterjegemeente.API.ServiceClient;
import com.example.justin.verbeterjegemeente.API.ServiceGenerator;
import com.example.justin.verbeterjegemeente.Adapters.SectionsPageAdapter;
import com.example.justin.verbeterjegemeente.Business.LocationSelectedListener;
import com.example.justin.verbeterjegemeente.domain.Service;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.justin.verbeterjegemeente.Constants.DEFAULT_LAT;
import static com.example.justin.verbeterjegemeente.Constants.DEFAULT_LONG;


public class MainActivity extends AppCompatActivity implements LocationSelectedListener {

    private static final String TAG = "MainActivity";

    private SectionsPageAdapter mSectionsPageAdapter;

    private ViewPager mViewPager;
    private Fab fabMenu;
    private Tab1Fragment tabFragment = new Tab1Fragment();
    private LatLng currentLatLng;
    private List<Service> serviceList;
    ArrayAdapter<String> catagoryAdapter;
    private ArrayList<String> catagoryList;
    private Spinner catagorySpinner;
    private ServiceClient client;

    private Locale myLocale;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: Starting.");

        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());






        Intent i = new Intent(getApplicationContext(), UpdateService.class);
        startService(i);















        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        catagoryList = new ArrayList<String>();
        catagoryAdapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_spinner_item, catagoryList);

        final FabSpeedDial fabSpeedDial = (FabSpeedDial) findViewById(R.id.activityMain_Fbtn_speeddial);
        fabSpeedDial.setMenuListener(new SimpleMenuListenerAdapter() {
            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.activityMain_item_filters :
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                        View mView = getLayoutInflater().inflate(R.layout.activity_main_filters_dialog, null);

                        SeekBar radius = (SeekBar) mView.findViewById(R.id.filterdialog_sb_radius);
                        radius.setMax(1000);
                        radius.incrementProgressBy(2);

                        final TextView radius_afstand = (TextView) mView.findViewById(R.id.filterdialog_tv_afstand6);
                        radius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                radius_afstand.setText(String.valueOf(progress) + "Meters" );
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {
                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                            }
                        });

                        // create an arraylist that will contain different categories fetched from an open311 interface
//                        catagoryList = new ArrayList<String>();
                        catagorySpinner = (Spinner) mView.findViewById(R.id.filterdialog_sp_categorieen);
                        /*catagoryAdapter = new ArrayAdapter<String>(getApplicationContext(),
                                android.R.layout.simple_spinner_item, catagoryList){
                            @Override //pakt de positions van elements in catagoryList en disabled the element dat postion null staat zodat we het kunnen gebruiken als een hint.
                            public boolean isEnabled(int position){
                                if (position == 0)
                                {
                                    return false;
                                }else{
                                    return true;
                                }
                            }
                        };*/
                        catagoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        catagorySpinner.setAdapter(catagoryAdapter);

                        builder1.setView(mView);
                        AlertDialog dialog = builder1.create();
                        dialog.show();
                        break;
                    case R.id.activityMain_item_report :
                        if (tabFragment.currentLatLng != null) {
                            double longCor = tabFragment.currentLatLng.longitude;
                            double latCor = tabFragment.currentLatLng.latitude;
                            currentLatLng = new LatLng(longCor, latCor);
                        }
                        Intent in = new Intent(getApplicationContext(),
                                com.example.justin.verbeterjegemeente.Presentation.MeldingActivity.class);
                        if (currentLatLng != null) {
                            in.putExtra("long", currentLatLng.longitude);
                            in.putExtra("lat", currentLatLng.latitude);
                        }
                        startActivity(in);
                        break;
                    case R.id.activityMain_item_gps :
                        Toast.makeText(getApplicationContext(), "GPS knop is aangeklikt",Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        try {
            if(ConnectionChecker.isConnected()) { // check if user is actually connected to the internet
                // create a callback
//                ServiceGenerator.changeApiBaseUrl("https://asiointi.hel.fi/palautews/rest/v1/");
                client = ServiceGenerator.createService(ServiceClient.class);
                Call<List<Service>> serviceCall = client.getServices(Constants.LANG_EN);
                // fire the get request
                serviceCall.enqueue(new Callback<List<Service>>() {
                    @Override
                    public void onResponse(Call<List<Service>> call, Response<List<Service>> response) {
                        // if a response has been received create a list with Services with the responsebody
                        serviceList = response.body();

                        // test what services have been caught in the response
                        if (serviceList != null) {
                            for (Service s : serviceList) {
                                Log.i("Response: ", "" + s.getService_name());
                            }

                        } else {
                            Log.i("Response: ", "List was empty");
                        }

                        if(serviceList != null) {
                            int x = 1; // set iterable separately for categoryList
                            for (int i = 0; i < serviceList.size(); i++) {
                                // first categoryList item is a default String
                                if(catagoryList.size() > 1) { // do something if list already has 1 or more categories
                                    // do something if previous category is not the same as new category in servicelist
                                    if(!catagoryList.get(x).equals(serviceList.get(i).getGroup())) {
                                        catagoryList.add(serviceList.get(i).getGroup()); // add new category
                                        x++; // only up this iterable if new category is added
                                    }
                                } else {
                                    catagoryList.add(serviceList.get(i).getGroup());
                                }
                            }

                            catagoryAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Service>> call, Throwable t) { // something went wrong

                        Toast.makeText(getApplication(), t.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });

            } else { // user is not connected to the internet
                Toast.makeText(getApplication(), getResources().getString(R.string.FoutOphalenProblemen),
                        Toast.LENGTH_SHORT).show();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

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
                            Toast.makeText(getApplication(), "NEDERLANDSE TAAL AANGEZET",Toast.LENGTH_SHORT).show();

                            setLocale("nl");
                            recreate();
                        }
                    });
                    Button engels = (Button) mView.findViewById(R.id.alertdialog_btn_engels);
                    engels.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(getApplication(), "ENGELSE TAAL AANGEZET",Toast.LENGTH_SHORT).show();

                            setLocale("en");
                            recreate();

                        }
                    });

                    SeekBar radius = (SeekBar) mView.findViewById(R.id.alertdialog_sb_radius);
                    final TextView radius_afstand = (TextView) mView.findViewById(R.id.alertdialog_tv_afstand);
                    radius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            radius_afstand.setText(String.valueOf(progress) + "Km." );
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
                public void onClick (View view) {
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







//            builder.setTitle("Profiel").setItems(new String[]
//                    {
//                            "Mijn meldingen", "Instellingen", "Over"
//                    }, new DialogInterface.OnClickListener()
//            {
//                public void onClick(DialogInterface dialog, int which) {
//                    switch (which) {
//                        case 0:
//                            Intent i = new Intent(getApplicationContext(), FollowingActivity.class);
//                            startActivity(i);
//                            break;
//                    }
//                }
//            });
//            builder.show();
//        }
//
//
//



    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(tabFragment, "");
        adapter.addFragment(new Tab2Fragment(), "");
        viewPager.setAdapter(adapter);
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Location currentLocation = null;
        GoogleMap mMap = tabFragment.mMap;
        GoogleApiClient mApiClient = tabFragment.mApiClient;
        switch (requestCode) {
            case 2: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }

                    if (mApiClient != null) {

                        // commented for testing purposed. Now jumps to default lat & long in Helsinki.
                        // uncomment this line
                        // currentLocation = LocationServices.FusedLocationApi.getLastLocation(mApiClient);

                        if (currentLocation != null) {
                            currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                        } else {
                            currentLatLng = new LatLng(DEFAULT_LONG, DEFAULT_LAT);
                        }
                        CameraUpdate center = CameraUpdateFactory.newLatLngZoom(currentLatLng, 16.0f);
                        mMap.moveCamera(center);
                    } else {
                        currentLatLng = new LatLng(DEFAULT_LONG, DEFAULT_LAT);
                        CameraUpdate center = CameraUpdateFactory.newLatLngZoom(currentLatLng, 16.0f);
                        mMap.moveCamera(center);

                    }
                    return;
                }
            }


        }
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
}
