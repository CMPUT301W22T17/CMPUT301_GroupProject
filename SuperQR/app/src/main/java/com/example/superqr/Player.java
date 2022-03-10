package com.example.superqr;

import android.location.Location;


/**
 * Player is the main interface by which most classes are managed by.
 */

public class Player {
    private PlayerSettings settings;
    private PlayerStats stats;
    private Location location;

    public Player(PlayerSettings settings, PlayerStats stats) {
        this.settings = settings;
        this.stats = stats;
    }

    public Player() {
        this.settings = new PlayerSettings();
        this.stats = new PlayerStats();
    }

    public PlayerSettings getSettings() {
        return settings;
    }
    
    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
