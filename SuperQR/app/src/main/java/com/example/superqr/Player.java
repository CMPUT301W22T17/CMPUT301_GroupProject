package com.example.superqr;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;


/**
 * Player is the main interface by which most classes are managed by.
 */

public class Player implements Serializable {
    private PlayerSettings settings;
    private PlayerStats stats;
    private Location playerlocation;

    public Player(PlayerSettings settings, PlayerStats stats) {
        this.settings = settings;
        this.stats = stats;
    }

    public Player() {
        this.settings = new PlayerSettings();
        this.stats = new PlayerStats();
        this.playerlocation = new Location("map_location");
    }

    public Location getPlayerLocation() {
        return this.playerlocation;
    }

    public PlayerSettings getSettings() {
        return settings;
    }

    public PlayerStats getStats() {
        return stats;
    }
    
    public Location getLocation() {
        return playerlocation;
    }

    public void setLocation(Location location) {
        this.playerlocation = location;
    }

}
