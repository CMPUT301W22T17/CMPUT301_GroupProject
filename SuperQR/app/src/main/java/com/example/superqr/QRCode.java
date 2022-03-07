package com.example.superqr;

import android.location.Location;

import java.util.ArrayList;

public class QRCode {
    private String hash;
    private int score;
    private Location location;
    private ArrayList<String> comments = new ArrayList<>();

    public QRCode(String hash, Location location) {
        this.hash = hash;
        this.location = location;
    }

    public void takePhoto(contents) {

    }

    public Boolean scanned() {
        return false;
    }


}
