package com.example.justin.verbeterjegemeente.domain;

/**
 * Created by Mika Krooswijk on 8-5-2017.
 */

public class Locatie {

    private double longitude, latitude;

    public Locatie(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(int longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(int latitude) {
        this.latitude = latitude;
    }
}
