package com.example.justin.verbeterjegemeente.Presentation;

import android.webkit.JavascriptInterface;

import com.google.android.gms.maps.model.LatLng;

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
    public void onCameraChanged(JSONObject LatLong) {
        //// TODO: 24-8-2017  Parse JSON object to LatLng
//        cameraListener.onCameraChanged();

    }

    @JavascriptInterface
    public void getMarkedLocation(JSONObject LatLong) {

    }

    interface OnCameraChangedListener {
        void onCameraChanged(LatLng CameraCoordinates);
    }

}
