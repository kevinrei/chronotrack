package com.kevinrei.chronotrack;

import android.os.Parcel;
import android.os.Parcelable;

public class Game implements Parcelable {
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

    public Game(Parcel source) {
        id = source.readInt();
        title = source.readString();
        image = source.readString();
        category = source.readString();
        unit = source.readString();
        recoveryRate = source.readInt();
        maxStamina = source.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(image);
        dest.writeString(category);
        dest.writeString(unit);
        dest.writeInt(recoveryRate);
        dest.writeInt(maxStamina);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Alarm> CREATOR = new Parcelable.Creator<Alarm>() {
        @Override
        public Alarm[] newArray(int size) {
            return new Alarm[size];
        }

        @Override
        public Alarm createFromParcel(Parcel source) {
            return new Alarm(source);
        }
    };
}

