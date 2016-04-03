package com.kevinrei.chronotrack;

import android.os.Parcel;
import android.os.Parcelable;

public class Alarm implements Parcelable {
    private int id;             // ID
    private int alarmId;        // Alarm ID
    private int gameId;         // Game ID
    private int flag;           // Layout flag
    private int start;          // Current stamina value
    private int end;            // Goal stamina value
    private long trigger;       // Exact time of trigger
    private long countdown;     // Start time of counter
    private String label;       // Alarm label
    private int save;           // Save (1) or delete (0)

    public Alarm() {}

    public Alarm(int id, int alarmId, int gameId, int flag, int start, int end,
                 long trigger, long countdown, String label, int save) {
        super();
        this.id = id;
        this.alarmId = alarmId;
        this.gameId = gameId;
        this.flag = flag;
        this.start = start;
        this.end = end;
        this.trigger = trigger;
        this.countdown = countdown;
        this.label = label;
        this.save = save;
    }

    /** Getters & setters */

    @Override
    public String toString() {
        return "Alarm [id="     + id            + ", " +
                "alarmId="      + alarmId       + ", " +
                "gameId="       + gameId        + ", " +
                "flag="         + flag          + ", " +
                "start="        + start         + ", " +
                "end="          + end           + ", " +
                "trigger="      + trigger       + ", " +
                "countdown="    + countdown     + ", " +
                "label="        + label         + ", " +
                "save="         + save          + "]";
    }

    public int getId() { return id; }
    public int getAlarmId() { return alarmId; }
    public int getGameId() { return gameId; }
    public int getFlag() { return flag; }
    public int getStart() { return start; }
    public int getEnd() { return end; }
    public long getTrigger() { return trigger; }
    public long getCountdown() { return countdown; }
    public String getLabel() { return label; }
    public int getSave() { return save; }

    public void setId(int id) { this.id = id; }
    public void setAlarmId(int alarmId) { this.alarmId = alarmId; }
    public void setGameId(int gameId) { this.gameId = gameId; }
    public void setFlag(int flag) { this.flag = flag; }
    public void setStart(int start) { this.start = start; }
    public void setEnd(int end) { this.end = end; }
    public void setTrigger(long trigger) { this.trigger = trigger; }
    public void setCountdown(long countdown) { this.countdown = countdown; }
    public void setLabel(String label) { this.label = label; }
    public void setSave(int save) { this.save = save; }

    /** Parcel */

    public Alarm(Parcel source) {
        id = source.readInt();
        alarmId = source.readInt();
        gameId = source.readInt();
        flag = source.readInt();
        start = source.readInt();
        end = source.readInt();
        trigger = source.readLong();
        countdown = source.readLong();
        label = source.readString();
        save = source.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(alarmId);
        dest.writeInt(gameId);
        dest.writeInt(flag);
        dest.writeInt(start);
        dest.writeInt(end);
        dest.writeLong(trigger);
        dest.writeLong(countdown);
        dest.writeString(label);
        dest.writeInt(save);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Alarm> CREATOR = new Creator<Alarm>() {
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