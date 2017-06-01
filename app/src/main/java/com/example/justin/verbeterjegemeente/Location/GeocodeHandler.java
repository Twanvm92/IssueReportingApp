package com.example.justin.verbeterjegemeente.Location;


import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.os.ResultReceiver;
import android.util.Log;
import android.widget.Toast;

import com.example.justin.verbeterjegemeente.domain.Locatie;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GeocodeHandler extends IntentService{

    private Geocoder gCoder;
    private ResultReceiver mReceiver;

    public GeocodeHandler() {
        super("geoCodeThread");
    }

    public void onCreate() {
        super.onCreate();
        gCoder = new Geocoder(getApplicationContext());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        //mReceiver = intent.getParcelableExtra("receiver");
        Log.i("coordinate", "" + intent.getDoubleExtra("long", 1));
        Log.i("coordinate", "" + intent.getDoubleExtra("lat", 1));
        double longitude = intent.getDoubleExtra("long", 1);
        double latitude = intent.getDoubleExtra("lat", 1);
        try {
            List<Address> addresses = gCoder.getFromLocation(latitude,longitude, 1);

            if(addresses != null) {

                Address fetchedAddress = addresses.get(0);
                StringBuilder strAddress = new StringBuilder();

                for(int i=0; i<fetchedAddress.getMaxAddressLineIndex(); i++) {
                    strAddress.append(fetchedAddress.getAddressLine(i)).append("\n");
                }
                Log.i("address", strAddress.toString());
                //deliverResultToReceiver(1, strAddress.toString());
            }
        } catch(IOException e) {

        }
    }

    private void deliverResultToReceiver(int resultCode, String message) {
        Bundle bundle = new Bundle();
        bundle.putString("Address", message);
        mReceiver.send(resultCode, bundle);
    }

}
