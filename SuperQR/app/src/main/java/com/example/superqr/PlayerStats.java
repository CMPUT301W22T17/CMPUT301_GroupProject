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
    private int totalScore;
    private QRCode highestScore = new QRCode();
    private QRCode lowestScore = new QRCode();

    /**
     * Creates a PlayerStats object
     *
     */
    public PlayerStats() {
        totalScore = 0;
        qrCodes = new ArrayList<>();
    }

    /**
     * Creates and PlayerStats object by providing info
     * @param qrCodes
     * @param totalScore
     * @param highestScore
     * @param lowestScore
     */
    public PlayerStats(ArrayList<QRCode> qrCodes, int totalScore, QRCode highestScore, QRCode lowestScore) {
        this.qrCodes = qrCodes;
        this.totalScore = totalScore;
        this.highestScore = highestScore;
        this.lowestScore = lowestScore;
    }

    protected PlayerStats(Parcel in) {
        qrCodes = in.createTypedArrayList(QRCode.CREATOR);
        totalScore = in.readInt();
        highestScore = in.readParcelable(QRCode.class.getClassLoader());
        lowestScore = in.readParcelable(QRCode.class.getClassLoader());
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
        return qrCodes.size();
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
     * Returns the highest scoring QR code of a player
     * @return
     *      Return the code
     */
    public QRCode getHighestScore() {
        return highestScore;
    }

    /**
     * Returns the lowest scoring QR code of a player
     * @return
     *      Return the code
     */
    public QRCode getLowestScore() {
        return lowestScore;
    }

    /**
     * Adds a QR code to the player's collection of QR codes and updates highest score,
     * lowest score, counts, and totalScore.
     * @param qrCode
     */
    public void addQrCode(QRCode qrCode) {
        int score = qrCode.getScore();

        this.qrCodes.add(qrCode);
        this.totalScore += score;

        if (this.qrCodes.size() == 1) { // First QR code
            highestScore = qrCode;
            lowestScore = qrCode;
        }
        else if (score > highestScore.getScore()) {
            highestScore = qrCode;
        }
        else if (score < lowestScore.getScore()) {
            lowestScore = qrCode;
        }
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeTypedList(qrCodes);
        parcel.writeInt(totalScore);
        parcel.writeParcelable(highestScore, i);
        parcel.writeParcelable(lowestScore, i);
    }
}
