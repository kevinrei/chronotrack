package com.kevinrei.chronotrack;

public class Alarm {
    private int aid;            // Alarm ID
    private String game;        // Game the alarm is for
    private int flag;           // Layout flag
    private int full;           // Stamina difference
    private long trigger;       // Trigger time
    private String label;       // Alarm label
    private int delete;         // Keep (0) or delete (1)

    public Alarm() {}

    public Alarm(int aid, String game, int flag, int full, long trigger, String label, int delete) {
        super();
        this.aid = aid;
        this.game = game;
        this.flag = flag;
        this.full = full;
        this.trigger = trigger;
        this.label = label;
        this.delete = delete;
    }

    /** Getters & setters */

    @Override
    public String toString() {
        return "Alarm [aid="    + aid       + ", " +
                "game="         + game      + ", " +
                "flag="         + flag      + ", " +
                "full="         + full      + ", " +
                "trigger="      + trigger   + ", " +
                "label="        + label     + ", " +
                "delete="       + delete    + "]";
    }

    public int getAid() { return aid; }
    public String getGame() { return game; }
    public int getFlag() { return flag; }
    public int getFull() { return full; }
    public long getTrigger() { return trigger; }
    public String getLabel() { return label; }
    public int getDelete() { return delete; }

    public void setAid(int aid) { this.aid = aid; }
    public void setGame(String game) { this.game = game; }
    public void setFlag(int flag) { this.flag = flag; }
    public void setFull(int full) { this.full = full; }
    public void setTrigger(long trigger) { this.trigger = trigger; }
    public void setLabel(String label) { this.label = label; }
    public void setDelete(int delete) { this.delete = delete; }
}
