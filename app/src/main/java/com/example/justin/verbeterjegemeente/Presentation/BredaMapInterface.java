package com.example.justin.verbeterjegemeente.Presentation;

import android.webkit.JavascriptInterface;

import org.json.JSONObject;

/**
 * Created by twanv on 16-8-2017.
 */

public class BredaMapInterface {

    @JavascriptInterface
    public void goToDetailedMelding(JSONObject ServiceRequest) {

    }

    @JavascriptInterface
    public void onCameraChanged(JSONObject LatLong) {

    }

    @JavascriptInterface
    public void getMarkedLocation(JSONObject LatLong) {

    }

}
