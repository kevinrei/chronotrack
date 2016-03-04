package com.kevinrei.chronotrack;

public class Game {
    private int id;
    private String title;
    private String unit;
    private int recoveryRate;
    private int maxStamina;

    public Game() {}

    public Game(String title, String unit, int recoveryRate, int maxStamina) {
        super();
        this.title = title;
        this.unit = unit;
        this.recoveryRate = recoveryRate;
        this.maxStamina = maxStamina;
    }

    /** Getters & setters */

    @Override
    public String toString() {
        return "Game [id="      + id            + ", " +
                "title="        + title         + ", " +
                "unit="         + unit          + ", " +
                "recoveryRate=" + recoveryRate  + ", " +
                "maxStamina="   + maxStamina    + "]";
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getUnit() { return unit; }
    public int getRecoveryRate() { return recoveryRate; }
    public int getMaxStamina() { return maxStamina; }

    public void setId(int id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setUnit(String unit) { this.unit = unit; }
    public void setRecoveryRate(int recoveryRate) { this.recoveryRate = recoveryRate; }
    public void setMaxStamina(int maxStamina) { this.maxStamina = maxStamina; }
}

