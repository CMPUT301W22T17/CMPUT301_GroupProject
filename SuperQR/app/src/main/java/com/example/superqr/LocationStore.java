package com.example.superqr;


public class LocationStore {

    private double latitude;
    private double longitude;

    public void LocationStore() {

    }

    public void setLocation(double newLatitude, double newLongitude) {
        this.latitude = newLatitude;
        this.longitude = newLongitude;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

}
