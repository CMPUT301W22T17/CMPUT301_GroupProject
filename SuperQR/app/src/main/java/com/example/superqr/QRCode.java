package com.example.superqr;

import android.location.Location;

import java.util.ArrayList;

/**
 * The QRCode class keeps track of the code, score, and geolocation of a QR code.
 * Takes photo of an object or location of the QR code.
 */
public class QRCode {
    private String code;
    private int score;
    private Location location;
    private Boolean scanned = false;
    private ArrayList<String> comments = new ArrayList<>();

    /**
     * Creates a QRCode object.
     * @param code
     *      QR code contents
     * @param location
     *      Geolocation of the QR code
     */
    public QRCode(String code, Location location) {
        // score will be calculated and stored
        this.code = code;
        this.location = location;
    }

    /**
     * Takes a photo of QR code, hashes the code and stores the hash.
     */
    public void takePhoto() {
        // WIP
    }

    /**
     * Scanned becomes true when the QR code has been scanned by another time
     */
    public void scanned() {
        this.scanned = true;
    }

    /**
     * Returns the string of a QR code
     * @return
     *      Return the code
     */
    public String getCode() {
        return code;
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

    public void setCode(String code) {
        this.code = code;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setScanned(Boolean scanned) {
        this.scanned = scanned;
    }

    public void setComments(ArrayList<String> comments) {
        this.comments = comments;
    }
}
