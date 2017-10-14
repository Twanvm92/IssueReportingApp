package com.example.justin.verbeterjegemeente.domain;

/**
 * Created by twanv on 24-8-2017.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Coordinates {
    @SerializedName("rx_x")
    @Expose
    private Double rxX;
    @SerializedName("rx_y")
    @Expose
    private Double rxY;
    @SerializedName("lat")
    @Expose
    private Double lat;
    @SerializedName("lon")
    @Expose
    private Double lon;
    @SerializedName("zoom")
    @Expose
    private Double zoom;

    public Coordinates() {
    }

    public Coordinates(Double lat, Double lon, Double zoom) {
        this.lat = lat;
        this.lon = lon;
        this.zoom = zoom;
    }

    public Double getRxX() {
        return rxX;
    }

    public void setRxX(Double rxX) {
        this.rxX = rxX;
    }

    public Double getRxY() {
        return rxY;
    }

    public void setRxY(Double rxY) {
        this.rxY = rxY;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public Double getZoom() {
        return zoom;
    }

    public void setZoom(Double zoom) {
        this.zoom = zoom;
    }

    public static boolean isZero(double value) {
        return value >= 0.0 && value <= 0.01;
    }
}
