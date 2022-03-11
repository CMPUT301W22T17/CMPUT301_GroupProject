package com.example.superqr;

import android.location.Location;


/**
 * Player is the main interface by which most classes are managed by.
 */

public class Player implements Comparable<Player>{
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
        this.location = new Location("map_location");
    }

    public Location getPlayerLocation() {
        return this.location;
    }

    public PlayerSettings getSettings() {
        return settings;
    }

    public PlayerStats getStats() {
        return stats;
    }
    
    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
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
