package com.example.justin.verbeterjegemeente.domain;

/**
 * Created by Mika Krooswijk on 8-5-2017.
 */

public class Locatie {

    private int longitude, latitude;

    public Locatie() {

    }

    public int getLongitude() {
        return longitude;
    }

    public void setLongitude(int longitude) {
        this.longitude = longitude;
    }

    public int getLatitude() {
        return latitude;
    }

    public void setLatitude(int latitude) {
        this.latitude = latitude;
    }
}
