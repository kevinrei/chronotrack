package com.kevinrei.chronotrack;

import java.io.Serializable;

public class Game implements Serializable {
    private int id;
    private String title;
    private String image;
    private String category;
    private String unit;
    private int recoveryRate;
    private int maxStamina;

    public Game() {}

    public Game(String title, String image, String category,
                String unit, int recoveryRate, int maxStamina) {
        super();
        this.title = title;
        this.image = image;
        this.category = category;
        this.unit = unit;
        this.recoveryRate = recoveryRate;
        this.maxStamina = maxStamina;
    }

    /** Getters & setters */

    @Override
    public String toString() {
        return "Game [id="      + id            + ", " +
                "title="        + title         + ", " +
                "image="        + image         + ", " +
                "category="     + category      + ", " +
                "unit="         + unit          + ", " +
                "recoveryRate=" + recoveryRate  + ", " +
                "maxStamina="   + maxStamina    + "]";
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getImage() { return image; }
    public String getCategory() { return category; }
    public String getUnit() { return unit; }
    public int getRecoveryRate() { return recoveryRate; }
    public int getMaxStamina() { return maxStamina; }

    public void setId(int id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setImage(String image) { this.image = image; }
    public void setCategory(String category) { this.category = category; }
    public void setUnit(String unit) { this.unit = unit; }
    public void setRecoveryRate(int recoveryRate) { this.recoveryRate = recoveryRate; }
    public void setMaxStamina(int maxStamina) { this.maxStamina = maxStamina; }
}

