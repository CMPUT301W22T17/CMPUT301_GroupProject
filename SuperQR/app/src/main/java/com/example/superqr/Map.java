package com.example.superqr;

import android.location.Location;

import com.example.superqr.Player;

import java.io.Serializable;
import java.util.ArrayList;

public class Map implements Serializable {
    protected ArrayList<Location> QRCodeLocations;
    protected Player player;

    /**
     * Initializes a map class by creating a list of QR codes
     */
    public Map() {
        this.QRCodeLocations = new ArrayList<Location>();
    }

    /**
     * Adds the locations of nearby QR codes to the map's database
     * @param QRLocation
     *      Location of QR code to be added to map's list of QR code locations
     */

    public void addAQRLocation(Location QRLocation) {
        this.QRCodeLocations.add(QRLocation);
    }

    /**
     * Adds a group of locations of nearby QR codes to the map's database
     * @param QRCodes
     *      Adds a list of QR code locations to the map's lsit of QR code locations
     */

    public void addQRLocations(ArrayList<QRCode> QRCodes) {
        for (QRCode code : QRCodes) {
            this.QRCodeLocations.add(code.getLocation());
        }
    }

    public ArrayList<Location> getQRLocations() {
        return QRCodeLocations;
    }
}
