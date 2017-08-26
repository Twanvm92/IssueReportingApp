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

public class BredaMapInterface {
    private OnCameraChangedListener cameraListener;

    public BredaMapInterface(OnCameraChangedListener cameraListener) {
        this.cameraListener = cameraListener;
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
    public void getMarkedLocation(JSONObject LatLong) {

    }

    interface OnCameraChangedListener {
        void onListenToCameraChanged(LatLng CameraCoordinates);
    }

}
