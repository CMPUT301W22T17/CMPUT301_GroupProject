package com.example.superqr;

import android.location.Location;

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

}
