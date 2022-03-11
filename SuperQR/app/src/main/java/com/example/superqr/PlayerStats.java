package com.example.superqr;

import java.util.ArrayList;

/**
 * The PlayerStats class act as
 * scoring system for a player
 */
public class PlayerStats {
    private ArrayList<QRCode> qrCodes;
    private int counts;
    private int totalScore;
    private int highestScore;
    private int lowestScore;

    /**
     * Creates an PlayerStats object
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

    public void setQrCodes(ArrayList<QRCode> qrCodes) {
        this.qrCodes = qrCodes;
    }

    public void setCounts(int counts) {
        this.counts = counts;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public void setHighestScore(int highestScore) {
        this.highestScore = highestScore;
    }

    public void setLowestScore(int lowestScore) {
        this.lowestScore = lowestScore;
    }
}