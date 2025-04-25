package com.example.tikiparkapp;

public class ParkingSpot {

    int id;
    String location;
    STATUS status;
    float pricePerHour;
    float latitude;
    float longitude;

    public ParkingSpot(int id, String location, STATUS status, float pricePerHour,
                       float latitude, float longitude) {
        this.id = id;
        this.location = location;
        this.status = status;
        this.pricePerHour = pricePerHour;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getId() {
        return id;
    }

    public String getLocation() {
        return location;
    }

    public STATUS getStatus() {
        return status;
    }

    public float getPricePerHour() {
        return pricePerHour;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }
}
