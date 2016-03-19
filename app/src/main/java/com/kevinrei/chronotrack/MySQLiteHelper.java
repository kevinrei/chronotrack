package com.kevinrei.chronotrack;

import android.database.sqlite.SQLiteDatabase;

        import android.content.ContentValues;
        import android.content.Context;
        import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.LinkedList;
        import java.util.List;

public class MySQLiteHelper extends SQLiteOpenHelper {

    private static final String TAG_GAME = "Game";
    private static final String TAG_ALARM = "Alarm";

    private static int DATABASE_VERSION = 1;                        // Database version
    private static final String DATABASE_NAME = "ChronoTrack";      // Database name

    /** Constants for games table & column names */
    private static final String TABLE_GAMES = "games";              // Table of games
    private static final String KEY_ID = "id";                      // ID of value
    private static final String KEY_TITLE = "title";                // Title
    private static final String KEY_IMAGE = "image";                // Image
    private static final String KEY_CATEGORY = "category";          // Category
    private static final String KEY_UNIT = "unit";                  // Unit
    private static final String KEY_RECOVERY_RATE = "recoveryRate"; // Recovery rate
    private static final String KEY_MAX_STAMINA = "maxStamina";     // Max stamina

    /** Constants for alarms table & column names */
    private static final String TABLE_ALARMS = "alarms";            // Table of alarms
    private static final String KEY_ALARM_ID = "alarmId";           // Alarm ID
    private static final String KEY_GAME_ID = "gameId";             // Game ID of alarm
    private static final String KEY_FLAG = "flag";                  // Layout flag
    private static final String KEY_START = "start";                // Current stamina
    private static final String KEY_END = "end";                    // Goal stamina
    private static final String KEY_TRIGGER = "trigger";            // Trigger time
    private static final String KEY_COUNTDOWN = "countdown";        // Countdown start time
    private static final String KEY_LABEL = "label";                // Alarm label
    private static final String KEY_SAVE = "save";                  // Save or delete

    private static final String[] COLUMNS_GAMES = {
            KEY_ID,
            KEY_TITLE,
            KEY_IMAGE,
            KEY_CATEGORY,
            KEY_UNIT,
            KEY_RECOVERY_RATE,
            KEY_MAX_STAMINA
    };

    private static final String[] COLUMNS_ALARMS = {
            KEY_ID,
            KEY_ALARM_ID,
            KEY_GAME_ID,
            KEY_FLAG,
            KEY_START,
            KEY_END,
            KEY_TRIGGER,
            KEY_COUNTDOWN,
            KEY_LABEL,
            KEY_SAVE
    };

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statements to create the tables
        String CREATE_GAMES_TABLE = "CREATE TABLE " + TABLE_GAMES + " (" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_TITLE + " TEXT, " +
                KEY_IMAGE + " TEXT, " +
                KEY_CATEGORY + " TEXT, " +
                KEY_UNIT + " TEXT, " +
                KEY_RECOVERY_RATE + " INTEGER, " +
                KEY_MAX_STAMINA + " INTEGER ); ";

        String CREATE_ALARMS_TABLE = "CREATE TABLE " + TABLE_ALARMS + " (" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_ALARM_ID + " INTEGER, " +
                KEY_GAME_ID + " INTEGER, " +
                KEY_FLAG + " INTEGER, " +
                KEY_START + " INTEGER, " +
                KEY_END + " INTEGER, " +
                KEY_TRIGGER + " INTEGER, " +
                KEY_COUNTDOWN + " INTEGER, " +
                KEY_LABEL + " TEXT, " +
                KEY_SAVE + " INTEGER ); ";

        // create names table
        db.execSQL(CREATE_GAMES_TABLE);
        db.execSQL(CREATE_ALARMS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older names table if it exists
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GAMES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALARMS);

        // Create a new names table
        this.onCreate(db);
    }

    /**
     * Games table CRUD methods
     * */

    // Add new details for a specific game
    public void addGame(Game game) {
        // Get reference to a writable database
        SQLiteDatabase db = this.getWritableDatabase();

        // Create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, game.getTitle());
        values.put(KEY_IMAGE, game.getImage());
        values.put(KEY_CATEGORY, game.getCategory());
        values.put(KEY_UNIT, game.getUnit());
        values.put(KEY_RECOVERY_RATE, game.getRecoveryRate());
        values.put(KEY_MAX_STAMINA, game.getMaxStamina());

        // Insert to database
        db.insert(TABLE_GAMES, null, values);

        Log.d(TAG_GAME, game.toString());

        // Close the database
        db.close();
    }

    // Get the details of a specific game
    public Game getGame(int id) {
        // Get reference to a readable database
        SQLiteDatabase db = this.getReadableDatabase();

        // Build the query
        Cursor cursor = db.query(TABLE_GAMES,           // table
                COLUMNS_GAMES,                          // column names
                " id = ?",                              // selections
                new String[] { String.valueOf(id) },    // selection arguments
                null,                                   // group by
                null,                                   // having
                null,                                   // order by
                null);                                  // limit

        // If the results are retrieved, get the first one
        if (cursor != null) { cursor.moveToFirst(); }

        // Build the Game object
        Game game = new Game();
        game.setId(Integer.parseInt(cursor.getString(0)));
        game.setTitle(cursor.getString(1));
        game.setImage(cursor.getString(2));
        game.setCategory(cursor.getString(3));
        game.setUnit(cursor.getString(4));
        game.setRecoveryRate(Integer.parseInt(cursor.getString(5)));
        game.setMaxStamina(Integer.parseInt(cursor.getString(6)));

        cursor.close();

        db.close();

        Log.d(TAG_GAME, game.toString());

        // Return the game
        return game;
    }

    // Get the list of all games
    public List<Game> getAllGames() {
        List<Game> gameList = new LinkedList<>();

        // Get reference to a readable database
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_GAMES, null, null, null, null, null, null);

        // Go over each row, build the row and add it to the list
        Game game;

        if (cursor.moveToFirst()) {
            do {
                game = new Game();
                game.setId(Integer.parseInt(cursor.getString(0)));
                game.setTitle(cursor.getString(1));
                game.setImage(cursor.getString(2));
                game.setCategory(cursor.getString(3));
                game.setUnit(cursor.getString(4));
                game.setRecoveryRate(Integer.parseInt(cursor.getString(5)));
                game.setMaxStamina(Integer.parseInt(cursor.getString(6)));

                // Add each game to gameList
                gameList.add(game);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        Log.d(TAG_GAME, gameList.toString());

        // Return the list of games
        return gameList;
    }

    // Update the game details
    public void updateGame(Game game, int appId) {
        // Get reference to a writable database
        SQLiteDatabase db = this.getWritableDatabase();

        // Create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, game.getTitle());
        values.put(KEY_IMAGE, game.getImage());
        values.put(KEY_CATEGORY, game.getCategory());
        values.put(KEY_UNIT, game.getUnit());
        values.put(KEY_RECOVERY_RATE, game.getRecoveryRate());
        values.put(KEY_MAX_STAMINA, game.getMaxStamina());

        // Update the row
        db.update(TABLE_GAMES,                              // table
                values,                                     // values
                KEY_ID + " = ?",                            // selections
                new String[]{String.valueOf(appId)});       // selection arguments

        // Close the database
        db.close();
    }

    public void deleteGame(Game game) {
        // Get reference to a writable database
        SQLiteDatabase db = this.getWritableDatabase();

        // Delete the row
        db.delete(TABLE_GAMES, KEY_ID + " = ?", new String[]{String.valueOf(game.getId())});

        // Close the database
        db.close();
    }


    /**
     * Alarms table CRUD methods
     * */

    // Add new details for an alarm
    public void addAlarm(Alarm alarm) {
        // Get reference to a writable database
        SQLiteDatabase db = this.getWritableDatabase();

        // Create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_ALARM_ID, alarm.getAlarmId());
        values.put(KEY_GAME_ID, alarm.getGameId());
        values.put(KEY_FLAG, alarm.getFlag());
        values.put(KEY_START, alarm.getStart());
        values.put(KEY_END, alarm.getEnd());
        values.put(KEY_TRIGGER, alarm.getTrigger());
        values.put(KEY_COUNTDOWN, alarm.getCountdown());
        values.put(KEY_LABEL, alarm.getLabel());
        values.put(KEY_SAVE, alarm.getSave());

        // Insert to database
        db.insert(TABLE_ALARMS, null, values);

        Log.d(TAG_ALARM, alarm.toString());

        // Close the database
        db.close();
    }

    // Get the details of an alarm
    public Alarm getAlarm(int aid) {
        // Get reference to a readable database
        SQLiteDatabase db = this.getReadableDatabase();

        // Build the query
        Cursor cursor = db.query(TABLE_ALARMS,          // table
                COLUMNS_ALARMS,                         // column names
                " aid = ?",                             // selections
                new String[] { String.valueOf(aid) },   // selection arguments
                null,                                   // group by
                null,                                   // having
                null,                                   // order by
                null);                                  // limit

        // If the results are retrieved, get the first one
        if (cursor != null) { cursor.moveToFirst(); }

        // Build the Alarm object
        Alarm alarm = new Alarm();
        alarm.setId(Integer.parseInt(cursor.getString(0)));
        alarm.setAlarmId(Integer.parseInt(cursor.getString(1)));
        alarm.setGameId(Integer.parseInt(cursor.getString(2)));
        alarm.setFlag(Integer.parseInt(cursor.getString(3)));
        alarm.setStart(Integer.parseInt(cursor.getString(4)));
        alarm.setEnd(Integer.parseInt(cursor.getString(5)));
        alarm.setTrigger(Long.parseLong(cursor.getString(6)));
        alarm.setCountdown(Long.parseLong(cursor.getString(7)));
        alarm.setLabel(cursor.getString(8));
        alarm.setSave(Integer.parseInt(cursor.getString(9)));

        cursor.close();

        db.close();

        Log.d(TAG_ALARM, alarm.toString());

        // Return the alarm
        return alarm;
    }

    // Get the list of all alarms
    public List<Alarm> getAllAlarms() {
        List<Alarm> alarmList = new LinkedList<>();

        // Get reference to a readable database
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ALARMS, null, null, null, null, null, null);

        // Go over each row, build the row and add it to the list
        Alarm alarm;

        if (cursor.moveToFirst()) {
            do {
                alarm = new Alarm();
                alarm.setId(Integer.parseInt(cursor.getString(0)));
                alarm.setAlarmId(Integer.parseInt(cursor.getString(1)));
                alarm.setGameId(Integer.parseInt(cursor.getString(2)));
                alarm.setFlag(Integer.parseInt(cursor.getString(3)));
                alarm.setStart(Integer.parseInt(cursor.getString(4)));
                alarm.setEnd(Integer.parseInt(cursor.getString(5)));
                alarm.setTrigger(Long.parseLong(cursor.getString(6)));
                alarm.setCountdown(Long.parseLong(cursor.getString(7)));
                alarm.setLabel(cursor.getString(8));
                alarm.setSave(Integer.parseInt(cursor.getString(9)));

                // Add each alarm to alarmList
                alarmList.add(alarm);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        Log.d(TAG_ALARM, alarmList.toString());

        // Return the list of alarms
        return alarmList;
    }

    // Get the list of all alarms
    public List<Alarm> getAlarmsForGame(int gameId) {
        List<Alarm> alarmList = new LinkedList<>();

        // Get reference to a readable database
        SQLiteDatabase db = this.getReadableDatabase();

        // Build the query
        Cursor cursor = db.query(TABLE_ALARMS,              // table
                COLUMNS_ALARMS,                             // column names
                " gameId = ?",                              // selections
                new String[] { String.valueOf(gameId) },    // selection arguments
                null,                                       // group by
                null,                                       // having
                null,                                       // order by
                null);                                      // limit

        // Go over each row, build the row and add it to the list
        Alarm alarm;

        if (cursor.moveToFirst()) {
            do {
                alarm = new Alarm();
                alarm.setId(Integer.parseInt(cursor.getString(0)));
                alarm.setAlarmId(Integer.parseInt(cursor.getString(1)));
                alarm.setGameId(Integer.parseInt(cursor.getString(2)));
                alarm.setFlag(Integer.parseInt(cursor.getString(3)));
                alarm.setStart(Integer.parseInt(cursor.getString(4)));
                alarm.setEnd(Integer.parseInt(cursor.getString(5)));
                alarm.setTrigger(Long.parseLong(cursor.getString(6)));
                alarm.setCountdown(Long.parseLong(cursor.getString(7)));
                alarm.setLabel(cursor.getString(8));
                alarm.setSave(Integer.parseInt(cursor.getString(9)));

                // Add each alarm to alarmList
                alarmList.add(alarm);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        Log.d(TAG_ALARM, alarmList.toString());

        // Return the list of alarms
        return alarmList;
    }

    // Update the alarm details
    public void updateAlarm(Alarm alarm, int alarmId) {
        // Get reference to a writable database
        SQLiteDatabase db = this.getWritableDatabase();

        // Create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_ALARM_ID, alarm.getAlarmId());
        values.put(KEY_GAME_ID, alarm.getGameId());
        values.put(KEY_FLAG, alarm.getFlag());
        values.put(KEY_START, alarm.getStart());
        values.put(KEY_END, alarm.getEnd());
        values.put(KEY_TRIGGER, alarm.getTrigger());
        values.put(KEY_COUNTDOWN, alarm.getCountdown());
        values.put(KEY_LABEL, alarm.getLabel());
        values.put(KEY_SAVE, alarm.getSave());

        // Update the row
        db.update(TABLE_ALARMS,                             // table
                values,                                     // values
                KEY_ID + " = ?",                           // selections
                new String[]{String.valueOf(alarmId)});     // selection arguments

        // Close the database
        db.close();
    }

    public void deleteAlarm(Alarm alarm) {
        // Get reference to a writable database
        SQLiteDatabase db = this.getWritableDatabase();

        // Delete the row
        db.delete(TABLE_ALARMS, KEY_ID + " = ?", new String[]{String.valueOf(alarm.getId())});

        // Close the database
        db.close();
    }
}
