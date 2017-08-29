package com.example.justin.verbeterjegemeente.Presentation;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.example.justin.verbeterjegemeente.domain.Coordinates;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import org.json.JSONObject;

/**
 * Created by twanv on 16-8-2017.
 */

// TODO: 27-8-2017 add javadoc
public class BredaMapInterface {
    private OnCameraChangedListener cameraListener;
    private OnMarkedLocationListener locationListener;

    public BredaMapInterface(OnCameraChangedListener cameraListener) {
        this.cameraListener = cameraListener;
    }

    public BredaMapInterface(OnMarkedLocationListener locationListener) {
        this.locationListener = locationListener;
    }

    @JavascriptInterface
    public void goToDetailedMelding(JSONObject ServiceRequest) {

    }

    @JavascriptInterface
    public void onCameraChanged(String latLong) {
        //// TODO: 24-8-2017  Parse JSON object to LatLng
        Gson gson = new Gson();
        Coordinates coordinates = gson.fromJson(latLong, Coordinates.class);
        LatLng latLng = new LatLng(coordinates.getLat(), coordinates.getLon());

        cameraListener.onListenToCameraChanged(latLng);

    }

    @JavascriptInterface
    public void getMarkedLocation(String latLong) {
        Log.i("JavascriptInterface: ", latLong);

        LatLng latLng = parseJsonCoordsToLatLng(latLong);

        locationListener.onMarkedLocation(latLng);
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
    interface OnCameraChangedListener {
        void onListenToCameraChanged(LatLng CameraCoordinates);
    }

    // TODO: 27-8-2017 add javadoc
    interface OnMarkedLocationListener {
        void onMarkedLocation(LatLng userChosenLocation);
    }

}
