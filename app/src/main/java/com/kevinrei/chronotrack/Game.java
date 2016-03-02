package com.kevinrei.chronotrack;

public class Game {
    private int id;
    private String title;
    private int recoveryRate;
    private int maxStamina;

    public Game() {}

    public Game(String title, int recoveryRate, int maxStamina) {
        super();
        this.title = title;
        this.recoveryRate = recoveryRate;
        this.maxStamina = maxStamina;
    }

    /** Getters & setters */

    @Override
    public String toString() {
        return "Game [id="      + id            + ", " +
                "title="        + title         + ", " +
                "recoveryRate=" + recoveryRate  + ", " +
                "maxStamina="   + maxStamina    + "]";
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public int getRecoveryRate() { return recoveryRate; }
    public int getMaxStamina() { return maxStamina; }

    public void setId(int new_id) { id = new_id; }
    public void setTitle(String new_title) { title = new_title; }
    public void setRecoveryRate(int new_recoveryRate) { recoveryRate = new_recoveryRate; }
    public void setMaxStamina(int new_maxStamina) { maxStamina = new_maxStamina; }
}

