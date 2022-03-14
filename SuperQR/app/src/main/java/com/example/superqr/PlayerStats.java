package com.example.superqr;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * The PlayerStats class act as
 * scoring system for a player
 */
public class PlayerStats implements Parcelable {
    private ArrayList<QRCode> qrCodes;
    private int counts;
    private int totalScore;
    private int highestScore;
    private int lowestScore;

    /**
     * Creates a PlayerStats object
     *
     */
    public PlayerStats() {
        counts = 0;
        totalScore = 0;
        highestScore = 0;
        lowestScore = 0;
        qrCodes = new ArrayList<>();
    }

    /**
     * Creates and PlayerStats object by providing info
     * @param qrCodes
     * @param counts
     * @param totalScore
     * @param highestScore
     * @param lowestScore
     */
    public PlayerStats(ArrayList<QRCode> qrCodes, int counts, int totalScore, int highestScore, int lowestScore) {
        this.qrCodes = qrCodes;
        this.counts = counts;
        this.totalScore = totalScore;
        this.highestScore = highestScore;
        this.lowestScore = lowestScore;
    }

    protected PlayerStats(Parcel in) {
        qrCodes = in.createTypedArrayList(QRCode.CREATOR);
        counts = in.readInt();
        totalScore = in.readInt();
        highestScore = in.readInt();
        lowestScore = in.readInt();
    }

    public static final Creator<PlayerStats> CREATOR = new Creator<PlayerStats>() {
        @Override
        public PlayerStats createFromParcel(Parcel in) {
            return new PlayerStats(in);
        }

        @Override
        public PlayerStats[] newArray(int size) {
            return new PlayerStats[size];
        }
    };

    /**
     * gets the QR code a player had scanned
     * @return
     *      qrCode list
     */
    public ArrayList<QRCode> getQrCodes() {
        return qrCodes;
    }

    /**
     * gets the amount of qrCodes a player has
     * @return
     *      counts integer
     */
    public int getCounts() {

        return counts;
    }

    /**
     * gets the total score of a player
     * @return
     *      totalScore integer
     */
    public int getTotalScore() {

        return totalScore;
    }

    /**
     * gets the highest score QR code of a player
     * @return
     *      highestScore integer
     */
    public int getHighestScore() {

        return highestScore;
    }

    /**
     * gets the lowest score QR code of a player
     * @return
     *      lowestScore integer
     */
    public int getLowestScore() {

        return lowestScore;
    }

    /**
     * adds a QR code to the player's collection of QR codes
     * @param qrCode
     */
    public void addQrCode(QRCode qrCode) {

        this.qrCodes.add(qrCode);

    }

    /**
     * increments number of QR codes the player has scanned
     */
    public void addCounts() {

        this.counts += 1;
    }

    /**
     * sets the total score of the player
     * @param score
     */
    public void addTotalScore(int score) {

        this.totalScore += score;
    }

    /**
     * sets the highest score of the player
     * @param highestScore
     */
    public void setHighestScore(int highestScore) {

        this.highestScore = highestScore;
    }

    /**
     * sets the lowest score of the player
     * @param lowestScore
     */
    public void setLowestScore(int lowestScore) {

        this.lowestScore = lowestScore;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeTypedList(qrCodes);
        parcel.writeInt(counts);
        parcel.writeInt(totalScore);
        parcel.writeInt(highestScore);
        parcel.writeInt(lowestScore);
    }
}
