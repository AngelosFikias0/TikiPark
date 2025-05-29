package com.example.tikiparkapp;

//Class used for the Google Maps API handling
public class ParkingSpot {

    String name;
    double lat;
    double lon;

    public ParkingSpot(String name, double lat, double lon) {
        this.name = name;
        this.lat = lat;
        this.lon = lon;
    }

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
