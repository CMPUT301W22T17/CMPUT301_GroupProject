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
        String contents = hash;
        String hash = Hasher.Companion.hash(contents, HashType.SHA_256);
        char[] charArray = contents.toCharArray();
        ArrayList<Character> duplicates = new ArrayList<Character>();
        for (int i = 0; i < contents.length(); i++) {
            for (int j = i + 1; j < contents.length(); j++) {
                if (charArray[i] == charArray[j]) {
                    duplicates.add(charArray[j]);
                    break;
                }
            }
        }

        ArrayList<Integer> dupeCount = new ArrayList<Integer>();
        for (int a = 0; a < charArray.length; a++) {
            int count = 0;
            for (int b = 0; b < contents.length(); b++) {
                if (contents.charAt(b) == charArray[a]) {
                    count++;
                }
            }
            dupeCount.add(count);
        }

        // this.score = hashedContents; // store this QRCode's score
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
