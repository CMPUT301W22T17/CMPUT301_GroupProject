package com.example.superqr;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.VisibleForTesting;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

/**
 * The QRCode class keeps track of the code, score, and geolocation of a QR code.
 * Takes photo of an object or location of the QR code.
 */
public class QRCode implements Parcelable {
    private String hash;
    private int score;
    private LocationStore location = new LocationStore();
    private ArrayList<String> comments = new ArrayList<>();
    private boolean scanned = false;

    // Firebase requirement
    public QRCode() {

    }

    /**
     * Creates a QRCode object.
     * @param code
     *      QR code to be hashed
     */
    public QRCode(String code) {
        try {
            this.hash = hashCode(code);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Exception thrown for incorrect algorithm: " + e);
        }
        this.score = calculateScore(this.hash);
    }

    protected QRCode(Parcel in) {
        hash = in.readString();
        score = in.readInt();
        location = in.readParcelable(LocationStore.class.getClassLoader());
        comments = in.createStringArrayList();
        scanned = in.readByte() != 0;
    }

    public static final Creator<QRCode> CREATOR = new Creator<QRCode>() {
        @Override
        public QRCode createFromParcel(Parcel in) {
            return new QRCode(in);
        }

        @Override
        public QRCode[] newArray(int size) {
            return new QRCode[size];
        }
    };

    @VisibleForTesting
    private String hashCode(String code) throws NoSuchAlgorithmException{
        // Hashes the code
        // https://www.geeksforgeeks.org/sha-256-hash-in-java/
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = md.digest(code.getBytes(StandardCharsets.UTF_8));
        BigInteger number = new BigInteger(1, hashBytes);
        StringBuilder hexString = new StringBuilder(number.toString(16));

        return hexString.toString();
    }

    /**
     * Calculates score for a given QR code's contents based on
     * SHA-256 hashing.
     * @param hash
     * @return
     */

    private int calculateScore(String hash) {
        // Calculate score
        // https://www.geeksforgeeks.org/java-program-for-hexadecimal-to-decimal-conversion/
        char[] hashChars = hash.toCharArray();
        char prevChar = hashChars[0];
        int duplicates = 0, totalScore = 0, base = 0;
        for (int i = 1; i < hash.length(); i++) {
            if (hashChars[i] == prevChar) {
                duplicates += 1;
            }
            else if (duplicates != 0) {
                if (prevChar == '0') {
                    base = 20;
                }
                else if (prevChar >= '1' && prevChar <= '9') { // 1-9
                    base = prevChar - 48; // 2-9
                }
                else if (prevChar >= 'a' && prevChar <= 'g') { // a-g
                    base = prevChar - 87; // 10-16
                }
                totalScore += Math.pow(base, duplicates); // base^duplicates
                duplicates = 0;
            }
            prevChar = hashChars[i];
        }

        return totalScore;
    }

    /**
     * Returns the hash of a QR code.
     * @return
     *      Return the code
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

    public LocationStore getStoreLocation() {
        return this.location;
    }

    /**
     * Returns if a QR code has been scanned before.
     * @return
     *      Return if the QR code was scanned
     */
    public boolean getScanned() {
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

    public void isScanned(boolean scanned) {
        this.scanned = true;
    }

    public void addComment(String comment) {
        this.comments.add(comment);
    }

    public void setLocation(double latitude, double longitude) {
        this.location.setLocation(latitude, longitude);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.hash);
        parcel.writeInt(this.score);
        parcel.writeList(this.comments);
        parcel.writeByte((byte) (this.scanned ? 1 : 0));
        parcel.writeParcelable(this.location, i);
    }
}
