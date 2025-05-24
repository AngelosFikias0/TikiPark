package com.example.tikiparkapp;

public class ParkingSpot {

    String name;
    double lat;
    double lon;
    float feePerHour;

    public ParkingSpot(String name, double lat, double lon) {
        this.name = name;
        this.lat = lat;
        this.lon = lon;
    }

    public float getFeePerHour() {return feePerHour;}

    public String getName() {
        return name;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }
}
