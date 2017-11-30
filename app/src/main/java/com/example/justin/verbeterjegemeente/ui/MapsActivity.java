package com.example.justin.verbeterjegemeente.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.justin.verbeterjegemeente.app.Constants;
import com.example.justin.verbeterjegemeente.R;
import com.example.justin.verbeterjegemeente.service.model.Coordinates;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static com.example.justin.verbeterjegemeente.app.Constants.REQUEST_CHECK_SETTINGS;

/**
 * De gebruiker moet hier een locatie kiezen die bij het probleem hoort
 * De gekozen locatie wordt aangegeven met een rode marker op de map
 */
public class MapsActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, BredaMapInterface.OnMarkedLocationListener,
        com.google.android.gms.location.LocationListener, BredaMapInterface.OnPageFullyLoadedListener {

    private static final String TAG = "MapsActivity: ";
    private WebView wbMap;
    private GoogleApiClient mApiClient;
    private ProgressBar progress;
    private LocationRequest mLocationRequest;
    private Coordinates currentCoordinates;

    /**
     * Savebutton geeft de LatLng mee aan MeldingActivity
     * Gps button zoekt de locatie van de gebruiker
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        currentCoordinates = new Coordinates();
        Intent in = getIntent();
        if (in.hasExtra("long") && in.hasExtra("lat") && in.hasExtra("zoom")) {
            double lon = in.getDoubleExtra("long", 1);
            double lat = in.getDoubleExtra("lat", 1);
            double zoom = in.getDoubleExtra("zoom", 1);
            Log.i(TAG, "current latlong: " + lat + ", " + lon + ", current zoom: " + zoom);
            currentCoordinates = new Coordinates(lat, lon, zoom);
        }

        wbMap = (WebView) findViewById(R.id.mapsActivity_wv_kaartBreda);
        progress = (ProgressBar) findViewById(R.id.mapsActivity_pb_webviewProgressbar);

        setUpMap();

        createLocationRequest();

        FloatingActionButton gpsButton = (FloatingActionButton) findViewById(R.id.activityMain_Fbtn_gps);
        gpsButton.setOnClickListener(v -> promptLocationSettings());


    }



    /**
     * Build the webview with additional settings and load a map from a URL.
     */
    // TODO: 8-8-2017 googlemap commented
    private void setUpMap() {
        //setup map settings
        wbMap.setVerticalScrollBarEnabled(false);
        wbMap.setHorizontalScrollBarEnabled(false);
        wbMap.addJavascriptInterface(new BredaMapInterface(this, this), "Android");
        wbMap.getSettings().setJavaScriptEnabled(true);
        wbMap.getSettings().setAllowFileAccessFromFileURLs(true);
        wbMap.getSettings().setAllowUniversalAccessFromFileURLs(true);
        wbMap.getSettings().setDomStorageEnabled(true);
        wbMap.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        wbMap.getSettings().setAppCachePath(this.getCacheDir().getAbsolutePath() );
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

            }
        });

        StringBuilder buf = new StringBuilder();
        InputStream json;
        try {
            json = this.getAssets().open("html/bredaKaartMapActivity");

            BufferedReader in;

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
        Log.i("Tab1Fragment", "Building GoogleApiClient");

        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    protected void createLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(30 * 1000);
        mLocationRequest.setFastestInterval(5 * 1000);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // only prompt location settings if this is the first time the user
        // wants to select a location for this service request.
        // TODO: 24-9-2017 Check why this is
        if (Coordinates.isZero(currentCoordinates.getLat())) {
            promptLocationSettings();
        }
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
     * Will try to retrieve users' last location after permission to access fine location
     * is granted. Will ask for user to give permission otherwise and onRequestPermissionResult
     * will be triggered.
     */
    public void getLastLocation() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // try to retrieve users' last location.
            Location location = LocationServices.FusedLocationApi.getLastLocation(mApiClient);
            if (location == null) {
                // something went wrong. Try to get a location update.
                LocationServices.FusedLocationApi.requestLocationUpdates(mApiClient, mLocationRequest, this);
            } else { // location was successfully retrieved
                Log.d(TAG, location.toString());

                currentCoordinates.setLon(location.getLongitude());
                currentCoordinates.setLat(location.getLatitude());
                zoomToLocation();
            }
        } else {
            // request the user for permission.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
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

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(this, locationSettingsResponse -> getLastLocation());

        task.addOnFailureListener(this, e -> {
            int statusCode = ((ApiException) e).getStatusCode();
            switch (statusCode) {
                case CommonStatusCodes.RESOLUTION_REQUIRED:
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialoggg.
                    try {
                        // Show the dialoggg by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(MapsActivity.this,
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
        });
    }

    @Override
    public void onLocationChanged(Location location) { // gets called when there is a location update from google play services
        // after updated location is available, make sure that location services does not keep updating locations
        LocationServices.FusedLocationApi.removeLocationUpdates(mApiClient, this);
        Log.d(TAG, "updated location: " + location.toString());

        currentCoordinates.setLon(location.getLongitude());
        currentCoordinates.setLat(location.getLatitude());

        zoomToLocation();
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
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)) {
                        // user has declined permission to access fine location at least once before
                        new AlertDialog.Builder(this) // show user why getting access to fine location is important
                                .setMessage(getResources().getText(R.string.eFineLocationPermissionExplain))
                                .setCancelable(false)
                                .setPositiveButton(getResources().getText(R.string.eImSure),
                                        (dialog, id) -> {
                                            /*// user decided not to give permission
                                            if (currentLatLng == null) {
                                                getDefaultLocation(); // use default location instead of users' location
                                            }*/
                                        })
                                .setNegativeButton(getResources().getText(R.string.eRetry), (dialog, which) -> {
                                    // user wants to try again
                                    promptLocationSettings();
                                })
                                .show();
                    } else { // user has not declined permission before
                        Log.i(TAG, "Geen toestemming gekregen, eerste keer dat map geladen wordt default lat/long gepakt");
//                        if (currentLatLng == null) {
//                        getDefaultLocation();
//                        }
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
                    new AlertDialog.Builder(this)
                            .setTitle(getString(R.string.eFindLocationTitle))
                            .setMessage(getString(R.string.eFindLocation))
                            .setPositiveButton(android.R.string.ok, (dialog, which) -> {

                            }).setIcon(android.R.drawable.ic_dialog_alert).show();

                }
                Log.i(TAG, "Gps aanvraag afgewezen");
        }
    }

    // TODO: 24-8-2017 add javadoc
    public void zoomToLocation(){
        if (!Coordinates.isZero(currentCoordinates.getLat())) {
            String lat = String.valueOf(currentCoordinates.getLat());
            String lng = String.valueOf(currentCoordinates.getLon());
            String zoom = String.valueOf(currentCoordinates.getZoom());

            wbMap.loadUrl("javascript:Geomerk.Map.zoomToLonLat(" + lng + "," + lat + "," + zoom + ")");
            Log.i(TAG, "Zoomed to a new location");
        }
    }

    /**
     * Ervoor zorgen dat teruggaan door middel van back press de marker alsnog meegegeven wordt
     */
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onMarkedLocation(LatLng userChosenLocation) {
        //create a new custom dialog
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.activity_maps_dialog, null);

        mBuilder.setView(mView);

        final TextView tvLocation = (TextView) mView.findViewById(R.id.activityMapsDialog_tv_location);
        tvLocation.setText("" + userChosenLocation.latitude + ", " + userChosenLocation.longitude);

        mBuilder.setTitle(getString(R.string.activityMain_item_gps));
        mBuilder.setPositiveButton(getString(R.string.eImSure), (dialog, which) -> {
//                Intent i = new Intent();
//
//                i.putExtra("long", userChosenLocation.longitude); //post longitude
//                i.putExtra("lat", userChosenLocation.latitude); //post latitude
//
//
//                setResult(RESULT_OK, i); //set result and return
//                finish();

            Intent in = new Intent(getApplicationContext(),
                    MeldingActivity.class);

            in.putExtra("long", userChosenLocation.longitude); //post longitude
            in.putExtra("lat", userChosenLocation.latitude); //post latitude

            startActivity(in);

        });
        mBuilder.setNegativeButton(getString(R.string.eRetry), (dialog, which) -> dialog.dismiss());

        mBuilder.setOnDismissListener(dialog -> wbMap.post(() -> wbMap.loadUrl("javascript:Geomerk.Map.drawGeo('Point', callbackGeom);")));

        final AlertDialog dialog = mBuilder.create();
        dialog.show();
        Log.i(TAG, "" + userChosenLocation.latitude + ", " + userChosenLocation.longitude);
    }


    @Override
    public void onPageFullyLoaded() {
        Log.i(TAG, "javainterface called");
        wbMap.post(() -> zoomToLocation());

    }
}
