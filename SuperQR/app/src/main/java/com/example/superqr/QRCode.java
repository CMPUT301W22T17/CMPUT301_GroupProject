package com.example.superqr;

import android.location.Location;

import java.util.ArrayList;

public class QRCode {
    private String hash;
    private int score;
    private Location location;
    private Boolean scanned = false;
    private ArrayList<String> comments = new ArrayList<>();

    public QRCode(String hash, Location location) {
        this.hash = hash;
        this.location = location;
    }

    public void takePhoto() {

    }

    public Boolean scanned() {
        return scanned;
    }


}
