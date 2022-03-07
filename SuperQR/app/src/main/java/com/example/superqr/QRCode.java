package com.example.superqr;

import android.location.Location;

import java.util.ArrayList;

/**
 * The QRCode class keeps track of the hash, score, and geolocation of a QR code.
 * Takes photo of a QR code.
 */
public class QRCode {
    private String hash;
    private int score;
    private Location location;
    private int scanned;
    private ArrayList<String> comments = new ArrayList<>();

    /**
     * Creates a QRCode object.
     * @param code
     *      QR code to be hashed
     * @param location
     *      Geolocation of the QR code
     */
    public QRCode(String code, Location location) {
        // hash and score will be calculated and stored
        this.location = location;
    }

    /**
     * Takes a photo of QR code, hashes the code and stores the hash.
     */
    public void takePhoto() {
        // WIP
    }

    /**
     * Increments how many times a QR code has been scanned.
     */
    public void scanned() {
        this.scanned += 1;
    }

    /**
     * Returns the hash of a QR code.
     * @return
     *      Return the hash
     */
    public String getHash() {
        return hash;
    }

    /**
     * Returns the score of a QR code.
     * @return
     *      Return the score
     */
    public int getScore() {
        return score;
    }

    /**
     * Returns the geolocation of a QR code.
     * @return
     *      Return the location
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Returns if a QR code has been scanned before.
     * @return
     *      Return if the QR code was scanned
     */
    public Boolean getScanned() {
        return this.scanned > 1;
    }

    /**
     * Returns an array of comments on a QR code.
     * @return
     *      Return the array
     */
    public ArrayList<String> getComments() {
        return comments;
    }
}
