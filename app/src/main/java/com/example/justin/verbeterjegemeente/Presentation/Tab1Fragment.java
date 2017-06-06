package com.example.justin.verbeterjegemeente.Presentation;



import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;

import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.justin.verbeterjegemeente.API.ConnectionChecker;
import com.example.justin.verbeterjegemeente.API.ServiceClient;
import com.example.justin.verbeterjegemeente.API.ServiceGenerator;
import com.example.justin.verbeterjegemeente.Business.BitmapGenerator;
import com.example.justin.verbeterjegemeente.Business.LocationSelectedListener;
import com.example.justin.verbeterjegemeente.Business.MarkerHandler;
import com.example.justin.verbeterjegemeente.R;
import com.example.justin.verbeterjegemeente.domain.ServiceRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;

import br.com.bloder.magic.view.MagicButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.justin.verbeterjegemeente.Constants.DEFAULT_LAT;
import static com.example.justin.verbeterjegemeente.Constants.DEFAULT_LONG;

/**
 * Created by Justin on 27-4-2017.
 */

public class Tab1Fragment extends SupportMapFragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener, GoogleMap.OnCameraIdleListener {
    public GoogleMap mMap;
    private ArrayList<Marker> markerList;
    private Marker marker;
    private Button button;
    private MarkerHandler mHandler;
    private MagicButton btnTEST;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2;
    private Location currentLocation;
    public LatLng currentLatLng;
    public GoogleApiClient mApiClient;
    public Marker currentMarker;
    ServiceClient client;
    private LocationSelectedListener locCallback;

    boolean popupShown = false;


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

//        ServiceGenerator.changeApiBaseUrl("https://asiointi.hel.fi/palautews/rest/v1/");
//        client = ServiceGenerator.createService(ServiceClient.class);
//
//        // create arraylist to contain created markers
//        markerList = new ArrayList<Marker>();
//
//        initApi();


//        service.getNearbyServiceRequests("")

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
//        try {
//            locCallback = (LocationSelectedListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnHeadlineSelectedListener");
//        }

    }

    public void onStart() {
        super.onStart();
        try {
            if (ConnectionChecker.isConnected()) {
                mApiClient.connect();
            } else {
                new AlertDialog.Builder(this.getContext())
                        .setTitle("No Internet Connection")
                        .setMessage("It looks like your internet connection is off. Please turn it " +
                                "on and try again")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).setIcon(android.R.drawable.ic_dialog_alert).show();
            }
        } catch (Exception e) {
            Log.i("Exception: ", e.getLocalizedMessage());
        }

    }

    public void onStop() {
        try {
            if (!ConnectionChecker.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mApiClient, this);
                if (mApiClient != null) {
                    mApiClient.disconnect();

                }
            }
        } catch(Exception e) {
            Log.i("EXCEPTION: ", e.getLocalizedMessage());
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
//        if (mMap == null) {
//            getMapAsync(this);
//        }
    }

    //set up map when map is loaded
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnCameraIdleListener(this);

        try {
            if(ConnectionChecker.isConnected()) {
                Call<ArrayList<ServiceRequest>> nearbyServiceRequests = client.getNearbyServiceRequests("" + DEFAULT_LONG, "" + DEFAULT_LAT, null, "300");
                nearbyServiceRequests.enqueue(new Callback<ArrayList<ServiceRequest>>() {
                    @Override
                    public void onResponse(Call<ArrayList<ServiceRequest>> call, Response<ArrayList<ServiceRequest>> response) {
                        if(response.isSuccessful()) {
                            ArrayList<ServiceRequest> srList = response.body();

                            for (ServiceRequest s : srList) {

                                mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(s.getLat(), s.getLong()))
                                        .title(s.getDescription())
                                        .icon(BitmapDescriptorFactory.fromBitmap(
                                                BitmapGenerator.getBitmapFromVectorDrawable(getContext(),
                                                        R.drawable.service_request_marker))));

                                Log.e("Opgehaalde servicereq: ", s.getDescription());
                            }

                        } else {
                            try { //something went wrong. Show the user what went wrong
                                JSONArray jObjErrorArray = new JSONArray(response.errorBody().string());
                                JSONObject jObjError = (JSONObject) jObjErrorArray.get(0);

                                Toast.makeText(getContext(), jObjError.getString("description"),
                                        Toast.LENGTH_SHORT).show();
                                Log.i("Error message: ", jObjError.getString("description"));
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<ServiceRequest>> call, Throwable t) {
                        Toast.makeText(getContext(), getResources().getString(R.string.ePostRequest),
                                Toast.LENGTH_SHORT).show();
                        t.printStackTrace();
                    }
                });
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        setUpMap();
        Log.e("MAP: ", "map is klaargezet");
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
    }

    public void initApi() {
        mApiClient = new GoogleApiClient.Builder(this.getContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        mApiClient.connect();
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




    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getLocation();
    }

    public void getLocation() {
        if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if(currentLocation != null) {
                currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            } else {


                // Location Breda. Can be used when Breda API is available.
                // currentLatLng = new LatLng(51.58656, 4.77596);

                // used to get Helsinki location for testing purposes
                currentLatLng = new LatLng(DEFAULT_LONG, DEFAULT_LAT);
                if(!popupShown) {
                    new AlertDialog.Builder(this.getContext())
                            .setTitle("Locatie bepalen mislukt")
                            .setMessage("het is niet gelukt uw huidige locatie te bepalen, mogelijk staat locatie voorziening uit, of is er geen internetverbinding. probeer het later opnieuw.")
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).setIcon(android.R.drawable.ic_dialog_alert).show();
                    popupShown = !popupShown;
                } else {
                    Toast.makeText(this.getContext(), "Locatie kon niet worden opgehaald", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            // get current location of user. Don`t use this one when testing Helsinki API.
            //  currentLocation = LocationServices.FusedLocationApi.getLastLocation(mApiClient);

            // used to get Helsinki location for testing purposes
            currentLatLng = new LatLng(DEFAULT_LONG, DEFAULT_LAT);
        }


        CameraUpdate center = CameraUpdateFactory.newLatLngZoom(currentLatLng, 16.0f);
        mMap.moveCamera(center);

        if (locCallback != null) {
            locCallback.locationSelected(currentLatLng);
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
        LatLng locationLatLng = new LatLng(location.getLongitude(), location.getLatitude());
        currentLatLng = locationLatLng;

        getLocation();
    }


    @Override
    public void onCameraIdle() {
        LatLng center = mMap.getCameraPosition().target;
        String camLat = "" + center.latitude;
        String camLng = "" + center.longitude;
        Log.e("Camera positie: ", "is veranderd");
        Call<ArrayList<ServiceRequest>> nearbyServiceRequests = client.getNearbyServiceRequests(camLat, camLng, null, "300");
        nearbyServiceRequests.enqueue(new Callback<ArrayList<ServiceRequest>>() {
            @Override
            public void onResponse(Call<ArrayList<ServiceRequest>> call, Response<ArrayList<ServiceRequest>> response) {
                if(response.isSuccessful()) {
                    ArrayList<ServiceRequest> srList = response.body();

                    for (ServiceRequest s : srList) {
                        mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(s.getLat(), s.getLong()))
                                .title(s.getDescription())
                                .icon(BitmapDescriptorFactory.fromBitmap(
                                        BitmapGenerator.getBitmapFromVectorDrawable(getContext(),
                                                R.drawable.service_request_marker)))
                        );
                        Log.e("Opgehaalde servicereq: ", s.getDescription());
                    }

                } else {
                    try { //something went wrong. Show the user what went wrong
                        JSONArray jObjErrorArray = new JSONArray(response.errorBody().string());
                        JSONObject jObjError = (JSONObject) jObjErrorArray.get(0);

                        Toast.makeText(getContext(), jObjError.getString("description"),
                                Toast.LENGTH_SHORT).show();
                        Log.i("Error message: ", jObjError.getString("description"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<ArrayList<ServiceRequest>> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage().toString(),
                        Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });

        if (locCallback != null) {
            locCallback.locationSelected(center);
        }

    }
}


