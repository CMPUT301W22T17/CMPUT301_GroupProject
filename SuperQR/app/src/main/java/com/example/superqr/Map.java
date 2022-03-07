package com.example.superqr;

import android.location.Location;

import java.util.ArrayList;

/**
 * Map is a class that displays the locations of nearby QR codes and the player's location
 */

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

    /**
     * Adds the locations of nearby QR codes to the map's database
     */

    public void addAQRLocation(Location QRLocation) {
        this.QRCodeLocations.add(QRLocation);
    }

    /**
     * Adds a group of locations of nearby QR codes to the map's database
     */

    public void addQRLocations(ArrayList<QRCode> QRCodes) {
        //WIP
        return;
    }

    /**
     * Gets the location of the current player
     */
    public void getPlayerLocation(ArrayList<QRCode> QRCodes) {
        // WIP
        return;
    }

    public ArrayList<Location> getQRLocations() {
        return QRCodeLocations;
    }

}
