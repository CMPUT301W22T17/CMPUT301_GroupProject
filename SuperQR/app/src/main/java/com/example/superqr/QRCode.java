package com.example.superqr;

import android.location.Location;

import com.himanshurawat.hasher.HashType;
import com.himanshurawat.hasher.Hasher;

import java.sql.Array;
import java.util.ArrayList;

/**
 * The QRCode class keeps track of the hash, score, and geolocation of a QR code.
 * Takes photo of a QR code.
 */
public class QRCode {
    private String hash;
    private int score;
    private Location location;
    private ArrayList<String> comments = new ArrayList<>();
    private boolean scanned;

    /**
     * Creates a QRCode object.
     * @param hash
     *      QR code to be hashed
     */
    public QRCode(String hash) {
        // hash and score will be calculated and stored
        this.hash = hash;
    }

    /**
     * Calculates the hash/score of a QR code.
     */

    public void hashContents() {
        int hashedContents = this.hash.hashCode();
        this.score = hashedContents; // store this QRCode's score
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
    public Boolean isScanned() {
        return scanned;
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
