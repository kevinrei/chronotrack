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

    public void setId(int new_id) { id = new_id; }
    public void setTitle(String new_title) { title = new_title; }
    public void setUnit(String new_unit) { unit = new_unit; }
    public void setRecoveryRate(int new_recoveryRate) { recoveryRate = new_recoveryRate; }
    public void setMaxStamina(int new_maxStamina) { maxStamina = new_maxStamina; }
}

