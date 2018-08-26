package com.example.justin.verbeterjegemeente.data;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by twanv on 14-1-2018.
 */

public interface DataManager {

    void saveData(String data);

    void saveData(LatLng location);

    String getData();

    LatLng getLocation();

}
