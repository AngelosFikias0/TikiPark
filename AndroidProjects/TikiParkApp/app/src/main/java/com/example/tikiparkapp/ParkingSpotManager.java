package com.example.tikiparkapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//Class used for the Google Maps API handling
public class ParkingSpotManager {

    HashMap<String, ParkingSpot> parkingSpots = new HashMap<>();
    List<String> parkingSpotNames = new ArrayList<>();

    public HashMap<String, ParkingSpot> getParkingSpots() {
        return parkingSpots;
    }

    public void addParkingSpot(String name, double lat, double lon) {
        parkingSpots.put(name, new ParkingSpot(name, lat, lon));
        parkingSpotNames.add(name);
    }

    public List<String> getParkingSpotNames() {
        return parkingSpotNames;
    }

    public ParkingSpot getParkingSpot(String name) {
        return parkingSpots.get(name);
    }
}
