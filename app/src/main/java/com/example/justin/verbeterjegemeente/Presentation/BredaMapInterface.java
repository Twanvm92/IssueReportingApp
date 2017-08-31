package com.example.justin.verbeterjegemeente.Presentation;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.example.justin.verbeterjegemeente.domain.Coordinates;
import com.example.justin.verbeterjegemeente.domain.ServiceRequest;
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
    private Context context;

    public BredaMapInterface(OnCameraChangedListener cameraListener, Context context) {
        this.cameraListener = cameraListener;
        this.context = context;
    }

    public BredaMapInterface(OnMarkedLocationListener locationListener) {
        this.locationListener = locationListener;
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
