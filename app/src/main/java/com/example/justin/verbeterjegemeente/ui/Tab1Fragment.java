package com.example.justin.verbeterjegemeente.ui;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.justin.verbeterjegemeente.API.RequestManager;
import com.example.justin.verbeterjegemeente.data.network.ServiceClient;
import com.example.justin.verbeterjegemeente.API.ServiceGenerator;
import com.example.justin.verbeterjegemeente.app.BitmapGenerator;
import com.example.justin.verbeterjegemeente.app.TimeStampGenerator;
import com.example.justin.verbeterjegemeente.app.Constants;
import com.example.justin.verbeterjegemeente.R;
import com.example.justin.verbeterjegemeente.service.model.Coordinates;
import com.example.justin.verbeterjegemeente.service.model.ServiceRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static com.example.justin.verbeterjegemeente.app.Constants.REQUEST_CHECK_SETTINGS;
import static com.example.justin.verbeterjegemeente.app.Constants.STATUS_OPEN;

// TODO: 8-8-2017 commented some code for google map
public class Tab1Fragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener,
        RequestManager.OnServiceRequestsReady, BredaMapInterface.OnCameraChangedListener {

    private static final String TAG = "Tab1Fragment: ";
    public GoogleMap mMap;
    public GoogleApiClient mApiClient;
    ServiceClient client;
    private ServiceRequestsReadyListener sRequestCallback;
    private String currentRadius;
    private String servCodeQ;
    private LocationRequest mLocationRequest;
    private WebView wbMap;
    private ProgressBar progress;
    private Coordinates currentCoordinates;


    public void onCreate(Bundle savedInstaceState) {
        super.onCreate(savedInstaceState);

        // get user selected radius and cat or use default radius and cat
        SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        int rValue = prefs.getInt(getString(R.string.activityMain_saved_radius), 20); // 20 is default
        currentRadius = Integer.toString(rValue);
        currentCoordinates = new Coordinates();

        servCodeQ = prefs.getString(getString(R.string.activityMain_saved_servcodeQ), null);

        client = ServiceGenerator.createService(ServiceClient.class);

        createLocationRequest();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab1_fragment, container, false);

        wbMap = (WebView) view.findViewById(R.id.tab1fragment_wv_kaartBreda);
        progress = (ProgressBar) view.findViewById(R.id.tab1fragment_pb_webviewProgressbar);

        setUpMap();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Activity a;

        if (context instanceof Activity) {
            a = (Activity) context;

            // This makes sure that the container activity has implemented
            // the callback interface. If not, it throws an exception
            try {
                sRequestCallback = (ServiceRequestsReadyListener) a;
            } catch (ClassCastException e) {
                throw new ClassCastException(a.toString()
                        + " must implement OnHeadlineSelectedListener");
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    /**
     * Build the webview with additional settings and load a map from a URL.
     */
    // TODO: 8-8-2017 googlemap commented
    private void setUpMap() {
        //setup map settings
        wbMap.setVerticalScrollBarEnabled(false);
        wbMap.setHorizontalScrollBarEnabled(false);
        wbMap.addJavascriptInterface(new BredaMapInterface(this, getContext()), "Android");
        wbMap.getSettings().setJavaScriptEnabled(true);
        wbMap.getSettings().setAllowFileAccessFromFileURLs(true);
        wbMap.getSettings().setAllowUniversalAccessFromFileURLs(true);
        wbMap.getSettings().setDomStorageEnabled(true);
        wbMap.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        wbMap.getSettings().setAppCachePath(getActivity().getCacheDir().getAbsolutePath() );
        wbMap.getSettings().setAllowFileAccess( true );
        wbMap.getSettings().setAppCacheEnabled( true );
        wbMap.getSettings().setCacheMode( WebSettings.LOAD_DEFAULT );
        wbMap.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                wbMap.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                // make sure gps fuctions are only being loaded when map is fully loaded
                buildGoogleApiClient();

                progress.setVisibility(View.GONE);
                wbMap.setVisibility(View.VISIBLE);
                Log.i(TAG, "kaart is geladen");
            }

            public boolean onConsoleMessage(ConsoleMessage cm) {
                Log.d("Console: ", cm.message() + " -- From line "
                        + cm.lineNumber() + " of "
                        + cm.sourceId() );
                return true;
            }
        });


        StringBuilder buf = new StringBuilder();
        InputStream json= null;
        try {
            json = getActivity().getAssets().open("html/bredaKaart.html");

            BufferedReader in= null;

            in = new BufferedReader(new InputStreamReader(json, "UTF-8"));
            String str;

            while ((str=in.readLine()) != null) {
                buf.append(str);
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        wbMap.loadDataWithBaseURL("file:///android_asset/", buf.toString(), "text/html", "utf-8", null);

//        wbMap.loadUrl("http://37.34.59.50/mapTest.html");

    }

    /**
     * Build the Google API Client that will be used to access Google Play Services
     */
    protected synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient");

        mApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .enableAutoManage(getActivity(), this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        promptLocationSettings();
    }

    @Override
    public void onConnectionSuspended(int i) {
        // nothing has to happen here
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // gets managed by api client automatically
    }

    /**
     * Create a location request that can be used later on to
     * prompt location settings for the user.
     * @see Tab1Fragment#promptLocationSettings()
     */
    protected void createLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(30 * 1000);
        mLocationRequest.setFastestInterval(5 * 1000);
    }

    /**
     * Will try to retrieve users' last location after permission to access fine location
     * is granted. Will ask for user to give permission otherwise and onRequestPermissionResult
     * will be triggered.
     */
    public void getLastLocation() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // try to retrieve users' last location.
            Location location = LocationServices.FusedLocationApi.getLastLocation(mApiClient);
            if (location == null) {
                // something went wrong. Try to get a location update.
                LocationServices.FusedLocationApi.requestLocationUpdates(mApiClient, mLocationRequest, Tab1Fragment.this);
            } else { // location was successfully retrieved
                Log.d(TAG, location.toString());

                LatLng locationCoords = new LatLng(location.getLatitude(), location.getLongitude());
                zoomToLocation(locationCoords);
            }
        } else {
            // request the user for permission.
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    Constants.MY_PERMISSIONS_LOCATION);
        }
    }

    /**
     * Prompt the location settings of the users' phone.
     * Will try to resolve the users' location settings if they are not satisfied.
     * Will try to get users' last location if location settings are satisfied
     */
    public void promptLocationSettings() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        SettingsClient client = LocationServices.getSettingsClient(getActivity());
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(getActivity(), new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                getLastLocation();
            }
        });

        task.addOnFailureListener(getActivity(), new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                int statusCode = ((ApiException) e).getStatusCode();
                switch (statusCode) {
                    case CommonStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialoggg.
                        try {
                            // Show the dialoggg by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(getActivity(),
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException sendEx) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialoggg.
                        break;
                }
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) { // gets called when there is a location update from google play services
        // after updated location is available, make sure that location services does not keep updating locations
        LocationServices.FusedLocationApi.removeLocationUpdates(mApiClient, this);
        Log.d("Tab1Fragment: ", "updated location: " + location.toString());
        LatLng locationCoords = new LatLng(location.getLatitude(), location.getLongitude());

        zoomToLocation(locationCoords);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Constants.MY_PERMISSIONS_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // received permission from user to access fine location
                    getLastLocation(); // location settings are already set so jump to getting last location
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION)) {
                        // user has declined permission to access fine location at least once before
                        new AlertDialog.Builder(getActivity()) // show user why getting access to fine location is important
                                .setMessage(getResources().getText(R.string.eFineLocationPermissionExplain))
                                .setCancelable(false)
                                .setPositiveButton(getResources().getText(R.string.eImSure),
                                        (dialog, id) -> {
                                            // user decided not to give permission
                                        })
                                .setNegativeButton(getResources().getText(R.string.eRetry), (dialog, which) -> {
                                    // user wants to try again
                                    promptLocationSettings();
                                })
                                .show();
                    } else { // user has not declined permission before
                        Log.i("onRequestPermResult", "Geen toestemming gekregen, eerste keer dat map geladen wordt default lat/long gepakt");
                    }

                }
            }
        }
    }

    // gets called after trying to resolve the users' location settings
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                if (resultCode == Activity.RESULT_OK) {
                    getLastLocation();
                } else {
                    new AlertDialog.Builder(this.getContext())
                            .setTitle(getString(R.string.eFindLocationTitle))
                            .setMessage(getString(R.string.eFindLocation))
                            .setPositiveButton(android.R.string.ok, (dialog, which) -> {

                            }).setIcon(android.R.drawable.ic_dialog_alert).show();

                }
                Log.i(TAG, "Gps aanvraag afgewezen");
        }
    }


    /**
     * This method will update the radius and service codes connected to the category
     * set by the user. After that it will get new service requests based on the new radius and
     * category filter and add them as markers on a Google map
     *
     * @param radius    radius in meters
     * @param servCodeQ String with service codes appending by a , delimiter
     *                  that can be used for filtering service requests.
     */
    public void updateRadiusCat(int radius, String servCodeQ) {
        currentRadius = Integer.toString(radius);
        this.servCodeQ = servCodeQ;

        if (Coordinates.isZero(currentCoordinates.getLat())) {
            Toast.makeText(getContext(), getString(R.string.errLoadingServiceRequestList), Toast.LENGTH_SHORT).show();
            return;
        }

        String currentLat = Double.toString(currentCoordinates.getLat());
        String currentLng = Double.toString(currentCoordinates.getLon());

        Log.i("servCodeq: ", "" + servCodeQ);
        Log.i(TAG, "currentlat: " + currentLat);
        Log.i(TAG, "currentlong: " + currentLng);

        // generate a timestamp of 2 weeks ago to get closed requests not more than 2 weeks old.
        String updateDatetime = TimeStampGenerator.genOlderTimestamp();

        // from here all the API requests will be handled
        RequestManager reqManager = new RequestManager(getActivity());
        // set callback for data passing
        reqManager.setOnServiceReqReadyCallb(this);

        if (servCodeQ != null && !servCodeQ.equals("")) {
            // launch Retrofit callback and retrieve services asynchronously
            reqManager.getServiceRequests(currentLat, currentLng, STATUS_OPEN, currentRadius, servCodeQ);
            // also request closed service request with an earliest update_datetime included.
            reqManager.getClosedServiceRequests(currentLat, currentLng, STATUS_OPEN, currentRadius, servCodeQ, updateDatetime);
        } else {
            // launch Retrofit callback and retrieve services asynchronously
            reqManager.getServiceRequests(currentLat, currentLng, STATUS_OPEN, currentRadius);
            // also request closed service request with an earliest update_datetime included.
            reqManager.getClosedServiceRequests(currentLat, currentLng, STATUS_OPEN, currentRadius, updateDatetime);
        }

    }

    /**
     * Add every newly received <code>ServiceRequest</code> to the Google map as a marker.
     * @param srList list of service requests obtained with Retrofit from an open311 Interface.
     */
    public void addServRequestsToMap(ArrayList<ServiceRequest> srList) {
        // clear all markers on the map before adding new markers
        mMap.clear();


        // loop through all the service requests and add their description
        // to a new marker on the Google map
        if (!srList.isEmpty()) {
            String status = srList.get(0).getStatus();
            if(!status.equals("closed")) { // add different markers to map based on status of SR
                for (ServiceRequest s : srList) {
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(s.getLat(), s.getLong()))
                            .title(s.getDescription())
                            .icon(BitmapDescriptorFactory.fromBitmap(
                                    BitmapGenerator.getBitmapFromVectorDrawable(getContext(),
                                            R.drawable.service_request_marker)))
                    );
                }
            } else if (status.equals("closed")) { // add different markers to map based on status of SR
                for (ServiceRequest s : srList) {
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(s.getLat(), s.getLong()))
                            .title(s.getStatus())
                    );
                }
            }
        }

    }

    @Override
    public void serviceRequestsReady(ArrayList<ServiceRequest> serviceRequests) {
        // pass received Service Requests to ActivityMain
        // ActivityMain will then pass the requests to Tab2Fragment
        sRequestCallback.onServiceRequestsReady(serviceRequests);

        if (!serviceRequests.isEmpty()) {
            for (ServiceRequest sr : serviceRequests) {

                String lat = String.valueOf(sr.getLat());
                String lng = String.valueOf(sr.getLong());

                Gson gson = new Gson();
                String serviceRequestJson = gson.toJson(sr);

                wbMap.loadUrl("javascript:Geomerk.Map.addPngLonLat(" + lng + ", " + lat + "," +
                        " 0.5, 46, 'http://openlayers.org/en/v3.7.0/examples/data/icon.png'," +
                        serviceRequestJson + ")");
                Log.i(TAG, "Service request added to map" );
            }
        }
    }

    // TODO: 24-8-2017 add javadoc
    // TODO: 24-9-2017 No special markers for Closed Service Requests yet
    public void testGettingServiceRequestsOnCameraChange() {
        //get Current radius selected by user in MainActivity
        Log.i("Current radius: ", currentRadius);

        if (Coordinates.isZero(currentCoordinates.getLat())) {
            Toast.makeText(getContext(), getString(R.string.errLoadingServiceRequestList), Toast.LENGTH_SHORT).show();
            return;
        }

        String camLat = Double.toString(currentCoordinates.getLat());
        String camLng = Double.toString(currentCoordinates.getLon());
        
        Log.i("Camera positie: ", "is veranderd");
        Log.i(TAG, "currentlat: " + camLat);
        Log.i(TAG, "currentlong: " + camLng);

        // generate a timestamp of 2 weeks ago to get closed requests not more than 2 weeks old.
        String updateDatetime = TimeStampGenerator.genOlderTimestamp();

        // from here all the API requests will be handled
        RequestManager reqManager = new RequestManager(getActivity());
        // set callback for data passing
        reqManager.setOnServiceReqReadyCallb(this);

        Log.i("servCodeq: ", "" + servCodeQ);

        if (servCodeQ != null && !servCodeQ.equals("")) {
            // launch Retrofit callback and retrieve services asynchronously
            reqManager.getServiceRequests(camLat, camLng, STATUS_OPEN, currentRadius, servCodeQ);
            // also request closed service request with an earliest update_datetime included.
            reqManager.getClosedServiceRequests(camLat, camLng, STATUS_OPEN, currentRadius, servCodeQ, updateDatetime);
        } else {
            // launch Retrofit callback and retrieve services asynchronously
            reqManager.getServiceRequests(camLat, camLng, STATUS_OPEN, currentRadius);
            // also request closed service request with an earliest update_datetime included.
            reqManager.getClosedServiceRequests(camLat, camLng, STATUS_OPEN, currentRadius, updateDatetime);
        }
    }

    // TODO: 24-8-2017 add javadoc
    public void zoomToLocation(LatLng currentLatLng){
        String lat = String.valueOf(currentLatLng.latitude);
        String lng = String.valueOf(currentLatLng.longitude);

        wbMap.loadUrl("javascript:Geomerk.Map.zoomToLonLat(" + lng + "," + lat + ",16)");
    }

    /**
     *
     * @return the current latitude and longitude of the camera on the map.
     */
    public Coordinates getCurrentCoordinates() {
        return currentCoordinates;
    }

    @Override
    public void onListenToCameraChanged(Coordinates cameraCoordinates) {
        // javascript functions can only be fired on UI thread.
        // create new runnable to fire javascript function on UI thread
        wbMap.post(() -> {
            // make sure to remove all previously loaded service requests on the map
            wbMap.loadUrl("javascript:Geomerk.Map.removeFeatures();");
            Log.i(TAG, "Service Requests have been removed from map");
        });

        currentCoordinates.setZoom(cameraCoordinates.getZoom());
        currentCoordinates.setLat(cameraCoordinates.getLat());
        currentCoordinates.setLon(cameraCoordinates.getLon());

        testGettingServiceRequestsOnCameraChange();
    }

    /**
     * Listener that passes a list of requested service requests to the MainActivity, so
     *  the MainActivity can pass them to Tab2Fragment.
     */
    interface ServiceRequestsReadyListener {
        void onServiceRequestsReady(ArrayList<ServiceRequest> srList);
    }

}

