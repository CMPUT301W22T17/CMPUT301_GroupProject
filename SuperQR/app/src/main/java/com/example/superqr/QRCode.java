package com.example.superqr;

import android.location.Location;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.*;


import com.google.android.gms.common.util.Hex;
import com.himanshurawat.hasher.HashType;
import com.himanshurawat.hasher.Hasher;

import java.io.Serializable;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Array;
import java.util.ArrayList;

import javax.crypto.KeyGenerator;

/**
 * The QRCode class keeps track of the code, score, and geolocation of a QR code.
 * Takes photo of an object or location of the QR code.
 */
public class QRCode implements Parcelable {
    private String hash;
    private int score;
    private Location location;
    private ArrayList<String> comments = new ArrayList<>();
    private boolean scanned = false;

    /**
     * Creates a QRCode object.
     * @param code
     *      QR code to be hashed
     */
    public QRCode(String code) {
        Log.d("debug", "Before hash");
        try {
            MessageDigest md = MessageDigest.getInstance("Sha-256");
            byte[] hashBytes = md.digest(code.getBytes(StandardCharsets.UTF_8));
            BigInteger number = new BigInteger(1, hashBytes);
            StringBuilder hexString = new StringBuilder(number.toString(16));
            while (hexString.length() < 32) {
                hexString.insert(0, '0');
            }
            this.hash = hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        Log.d("debug", hash);

    }

    protected QRCode(Parcel in) {
        hash = in.readString();
        score = in.readInt();
        location = in.readParcelable(Location.class.getClassLoader());
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

    /**
     * Calculates the hash/score of a QR code.
     */

    public void hashContents() {
        Log.d("debug", "Scoring");
        char[] charArray = hash.toCharArray();
        ArrayList<Character> duplicates = new ArrayList<Character>();
        for (int i = 0; i < hash.length(); i++) {
            for (int j = i + 1; j < hash.length(); j++) {
                if (charArray[i] == charArray[j]) {
                    duplicates.add(charArray[j]);
                    break;
                }
            }
        }

        ArrayList<Integer> dupeCount = new ArrayList<Integer>();
        for (int a = 0; a < charArray.length; a++) {
            int count = 0;
            for (int b = 0; b < hash.length(); b++) {
                if (hash.charAt(b) == charArray[a]) {
                    count++;
                }
            }
            dupeCount.add(count);
        }

        for (int c = 0; c < duplicates.size(); c++) {
            if (duplicates.get(c) == 0) {
                int total = (int) Math.pow(20, dupeCount.get(c) - 1);
                this.score += total;
            }
            else {
                int number = Integer.parseInt(String.valueOf(duplicates.get(c)), 16);
                int total = (int) Math.pow(number, dupeCount.get(c) - 1);
                this.score += total;
            }
        }
        System.out.println(this.score);
    }

    /**
     * Returns the string of a QR code
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
    public Location getLocation() {
        return location;
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

    public void setScanned(Boolean scanned) {
        this.scanned = scanned;
    }

    public void setComments(ArrayList<String> comments) {
        this.comments = comments;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(hash);
        parcel.writeInt(this.score);
        parcel.writeList(this.comments);
        parcel.writeByte((byte) (this.scanned ? 1 : 0));
        parcel.writeParcelable(this.location, i);


    }
}
