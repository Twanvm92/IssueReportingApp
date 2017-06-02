package com.example.justin.verbeterjegemeente.Location;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.os.ResultReceiver;

import com.google.android.gms.maps.model.LatLng;

public class GeoLocator extends ResultReceiver{
    static GeocodeHandler gHandler = new GeocodeHandler();
    static String address;

    public GeoLocator(Handler handler) {
        super(handler);
    }

    public static String getLocation(LatLng location, Context context) {
        try {
            Intent intent = new Intent(context, GeocodeHandler.class);
            intent.putExtra("long", location.longitude);
            intent.putExtra("lat", location.latitude);
            gHandler.startService(intent);

            while (address == null) {

            }
            return address;
        }catch (Exception e){
            return "no address found";
        } finally {
            setAddress(null);
        }
    }

    private static void setAddress(String input) {
        address = input;
    }
    /*
    public static void onReceiveResult(int resultcode, Bundle data) {

    }
    */


}
