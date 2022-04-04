package com.example.superqr;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.Random;


/**
 * Player is the main interface by which most classes are managed by.
 */

public class Player implements Parcelable, Comparable<Player> {
    private PlayerSettings settings;
    private PlayerStats stats = new PlayerStats();
    private LocationStore location = new LocationStore();
    private String playerID = generateID();
    private Boolean isAdmin = false;

    /**
     * empty constructor needed for Firebase
     */
    public Player() {
    }

    /**
     * Constructor for player using username, phone and email
     * @param userName
     * player's username
     * @param phone
     * player's phone
     * @param email
     * player's email
     */
    public Player(String userName, String phone, String email) {
        this.settings = new PlayerSettings(userName, phone, email);
    }

    protected Player(Parcel in) {
        settings = in.readParcelable(PlayerSettings.class.getClassLoader());
        stats = in.readParcelable(PlayerStats.class.getClassLoader());
        location = in.readParcelable(LocationStore.class.getClassLoader());
        playerID = in.readString();
        isAdmin = in.readByte() != 0;

    }

    public static final Creator<Player> CREATOR = new Creator<Player>() {
        @Override
        public Player createFromParcel(Parcel in) {
            return new Player(in);
        }

        @Override
        public Player[] newArray(int size) {
            return new Player[size];
        }
    };

    /**
     * Return player location
     * @return Player Location
     */
    public LocationStore getLocation() {
        return this.location;
    }

    /**
     * Set player location
     * @param latitude
     * player's latitude
     * @param longitude
     * player's longitude
     */
    public void setPlayerLocation(double latitude, double longitude){
        this.location.setLocation(latitude, longitude);
    }

    /**
     * @return Player's PlayerSettings
     */
    public PlayerSettings getSettings() {
        return this.settings;
    }

    /**
     * @return Player's PlayerStat
     */
    public PlayerStats getStats() {
        return this.stats;
    }

    public void setSettings(PlayerSettings settings) {
        this.settings = settings;

    }

    public String getPlayerID() {
        return playerID;
    }

    public void setStats(PlayerStats stats) {
        this.stats = stats;
    }

    //needed for parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    //needed for parcelable
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(settings, i);
        parcel.writeParcelable(stats, i);
        parcel.writeParcelable(location, i);
        parcel.writeString(playerID);
        parcel.writeByte((byte) (this.isAdmin ? 1 : 0));
    }

    /**
     * Comparator method used to sort array lists.
     * @param player to be compared with
     * @return 0 if both players have the same score
     *         1  if player score is greather than this score
     *        -1 if this score is greather than player score
     */
    @Override
    public int compareTo(Player player) {
        if (this.stats.getTotalScore() == player.stats.getTotalScore()) {
            return 0;
        } else if (this.stats.getTotalScore() > player.stats.getTotalScore()) {
            return -1;
        } else {
            return 1;
        }
    }

    /**
     * generate a random string that represents player ID
     *
     * @return String
     */
    private String generateID() {
        // https://www.programiz.com/java-programming/examples/generate-random-string
        String upperAlphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerAlphabet = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";
        String alphaNumeric = upperAlphabet + lowerAlphabet + numbers;

        StringBuilder sb = new StringBuilder();

        Random random = new Random();

        int length = 10;

        for (int i = 0; i < length; i++) {
            int randomInt = random.nextInt(alphaNumeric.length());

            char randomChar = alphaNumeric.charAt(randomInt);

            sb.append(randomChar);
        }
        return sb.toString();
    }

    /**
     * check if player is the owner, return true if they are, false otherwise
     * @return Boolean isAdmin
     */
    public Boolean getIsAdmin() {
        return isAdmin;
    }
}
