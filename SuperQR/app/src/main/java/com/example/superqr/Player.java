package com.example.superqr;


import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;


/**
 * Player is the main interface by which most classes are managed by.
 */

public class Player implements Parcelable, Comparable<Player> {
    private PlayerSettings settings;
    private PlayerStats stats = new PlayerStats();
    private Location location = new Location("map_location");

    public Player() {
    }

    public Player(String userName, String phone, String email) {
        this.settings = new PlayerSettings(userName, phone, email);
    }

    protected Player(Parcel in) {
        settings = in.readParcelable(PlayerSettings.class.getClassLoader());
        stats = in.readParcelable(PlayerStats.class.getClassLoader());
        location = in.readParcelable(Location.class.getClassLoader());
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

    public Location getPlayerLocation() {
        return this.location;
    }

    public void setPlayerLocation(double latitude, double longitude){
        this.location.setLatitude(latitude);
        this.location.setLongitude(longitude);
    }

    public PlayerSettings getSettings() {
        return this.settings;
    }

    public PlayerStats getStats() {
        return this.stats;
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(settings, i);
        parcel.writeParcelable(stats, i);
        parcel.writeParcelable(location, i);
    }

    @Override
    public int compareTo(Player player) {
        if (this.stats.getHighestScore() == player.stats.getHighestScore()) {
            return 0;
        } else if (this.stats.getHighestScore() > player.stats.getHighestScore()) {
            return -1;
        } else {
            return 1;
        }
    }

}
