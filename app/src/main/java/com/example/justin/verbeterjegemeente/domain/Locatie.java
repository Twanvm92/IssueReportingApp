package com.example.justin.verbeterjegemeente.domain;

/** <code>Locatie</code> is a class that saves the location of a user
 * in longtitude and latitude.
 * @author Mika Krooswijk
 * @author Twan van Maastricht
 */

public class Locatie {

    private double longitude, latitude;

    public Locatie(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
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
