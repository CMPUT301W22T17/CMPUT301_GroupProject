package com.example.superqr;

import android.location.Location;

import java.util.ArrayList;

public class Map {
    protected ArrayList<Location> QRCodeLocations;
    protected Player player;

    public Map(Player currentPlayer) {
        this.player = currentPlayer;
        this.QRCodeLocations = new ArrayList<Location>();
    }

    // test constructor (remove once other classes are built more)
    public Map() {
        this.QRCodeLocations = new ArrayList<Location>();
    }
}
