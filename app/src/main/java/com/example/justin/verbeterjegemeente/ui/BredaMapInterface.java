package com.example.justin.verbeterjegemeente.ui;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.example.justin.verbeterjegemeente.service.model.Coordinates;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import timber.log.Timber;

/**
 * Created by twanv on 16-8-2017.
 */

// TODO: 27-8-2017 add javadoc
public class BredaMapInterface {
    private OnCameraChangedListener cameraListener;
    private OnMarkedLocationListener locationListener;
    private OnPageFullyLoadedListener pageFullyLoadedListener;
    private OnShowSnackbarListener snackbarListener;
    private OnZoomReadyListener zoomReadyListener;
    private final String TAG = "BredaMapInterface: ";

    public BredaMapInterface(OnCameraChangedListener cameraListener,
                             OnPageFullyLoadedListener pageFullyLoadedListener,
                             OnShowSnackbarListener snackbarListener,
                             OnMarkedLocationListener locationListener,
                             OnZoomReadyListener zoomReadyListener) {
        this.cameraListener = cameraListener;
        this.pageFullyLoadedListener = pageFullyLoadedListener;
        this.snackbarListener = snackbarListener;
        this.locationListener = locationListener;
        this.zoomReadyListener = zoomReadyListener;
    }

    public BredaMapInterface(OnMarkedLocationListener locationListener, OnPageFullyLoadedListener pageFullyLoadedListener) {
        this.locationListener = locationListener;
        this.pageFullyLoadedListener = pageFullyLoadedListener;
    }

    @JavascriptInterface
    public void goToDetailedMelding(String serviceRequest) {
        /*Gson gson = new Gson();
        ServiceRequest sr = gson.fromJson(serviceRequest, ServiceRequest.class);

        Intent i = new Intent(context, DetailedMeldingActivity.class);
        i.putExtra("serviceRequest", sr);
        context.startActivity(i);*/

        Log.i("JavascriptInterface: ", serviceRequest);
    }

    // added Zoom data to position object in _onMoveEnd in js libary !!
    @JavascriptInterface
    public void onCameraChanged(String latLong) {
        Gson gson = new Gson();
        Coordinates coordinates = gson.fromJson(latLong, Coordinates.class);

        Log.i(TAG, "zoomlevel: " + coordinates.getZoom());

        cameraListener.onListenToCameraChanged(coordinates);

    }

    @JavascriptInterface
    public void getMarkedLocation(String latLong) {
        Log.i("JavascriptInterface: ", latLong);

        LatLng latLng = parseJsonCoordsToLatLng(latLong);

        locationListener.onMarkedLocation(latLng);
    }

    @JavascriptInterface
    public void pageIsReady() {
        Timber.d("Page is fully loaded");
        pageFullyLoadedListener.onPageFullyLoaded();
    }

    @JavascriptInterface
    public void showSnackbar(String message) {
        Timber.d("Snackbar being build");
        snackbarListener.onShowSnackbar(message);
    }

    @JavascriptInterface
    public void zoomIsReady(String status) {
        Boolean bool = false;
        if (status.equals("true")) {
            bool = true;
        }
        Timber.d("zoom is " + status);
        zoomReadyListener.onZoomReady(bool);
    }


    // TODO: 27-8-2017 add javadoc
    private LatLng parseJsonCoordsToLatLng(String json) {
        int indexOfComma = json.indexOf(",");
        int lenghtOfString = json.length();
        String lng = json.substring(1,indexOfComma);
        String lat = json.substring(indexOfComma + 1, lenghtOfString - 1);
        double dLng = Double.parseDouble(lng);
        double dLat = Double.parseDouble(lat);
        return new LatLng(dLat, dLng);
    }

    // TODO: 27-8-2017 add javadoc
    public interface OnCameraChangedListener {
        void onListenToCameraChanged(Coordinates cameraCoordinates);
    }

    // TODO: 27-8-2017 add javadoc
    public interface OnMarkedLocationListener {
        void onMarkedLocation(LatLng userChosenLocation);
    }

    public interface OnPageFullyLoadedListener {
        void onPageFullyLoaded();
    }

    public interface OnShowSnackbarListener {
        void onShowSnackbar(String message);
    }

    public interface OnZoomReadyListener {
        void onZoomReady(Boolean status);
    }

}
