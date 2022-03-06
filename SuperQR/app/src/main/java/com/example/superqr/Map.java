package com.example.superqr;

import java.util.ArrayList;

/**
 * Map is a class that displays the locations of nearby QR codes and the player's location
 */

public class Map {
    private ArrayList<QRCode> QRCodeLocations;
    private Player player;

    public Map(ArrayList<QRCode> QRLocations, Player currentPlayer) {
        QRCodeLocations = QRLocations;
        player = currentPlayer;
    }

    /**
     * Displays the map for the player to see
     */
    public void displayMap() {
        // WIP
        return;
    }

    /**
     * Places the locations of nearby QR codes on the map
     */
    public void placeQRLocations() {
        // WIP
        return;
    }

    /**
     * Places the location of the current player
     */
    public void placePlayerLocation() {
        // WIP
    }

}
