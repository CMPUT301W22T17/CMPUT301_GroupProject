package com.example.superqr;

import android.location.Location;


/**
 * Player is the main interface by which most classes are managed by.
 */

public class Player implements Comparable<Player>{
    private int score;
    private String name;

    public Player(String name, int score) {
        this.name = name;
        this.score = score;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int compareTo(Player player) {
        if (this.score == player.getScore()) {
            return 0;
        } else if (this.score > player.getScore()) {
            return -1;
        } else {
            return 1;
        }
    }
}
