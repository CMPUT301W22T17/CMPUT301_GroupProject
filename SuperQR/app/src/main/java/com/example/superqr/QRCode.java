package com.example.superqr;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import com.himanshurawat.hasher.HashType;
import com.himanshurawat.hasher.Hasher;

import java.io.Serializable;
import java.sql.Array;
import java.util.ArrayList;

/**
 * The QRCode class keeps track of the code, score, and geolocation of a QR code.
 * Takes photo of an object or location of the QR code.
 */
public class QRCode implements Parcelable {
    private String code;
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
        // hash and score will be calculated and stored
        this.code = code;
    }

    protected QRCode(Parcel in) {
        code = in.readString();
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
        String contents = code;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.code);
        parcel.writeInt(this.score);
        parcel.writeList(this.comments);
        parcel.writeByte((byte) (this.scanned ? 1 : 0));
        parcel.writeParcelable(this.location, i);


    }
}
