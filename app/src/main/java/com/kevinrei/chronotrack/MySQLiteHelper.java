package com.kevinrei.chronotrack;

import android.database.sqlite.SQLiteDatabase;

        import android.content.ContentValues;
        import android.content.Context;
        import android.database.Cursor;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteOpenHelper;

        import java.util.LinkedList;
        import java.util.List;

public class MySQLiteHelper extends SQLiteOpenHelper {

    private static int DATABASE_VERSION = 1;                        // Database version
    private static final String DATABASE_NAME = "ChronoTrack";      // Database name

    /** Constants for persons table & column names */
    private static final String TABLE_GAMES = "games";              // Table of games
    private static final String KEY_ID = "id";                      // ID of value
    private static final String KEY_TITLE = "title";                // Title
    private static final String KEY_IMAGE = "image";                // Image
    private static final String KEY_CATEGORY = "category";          // Category
    private static final String KEY_UNIT = "unit";                  // Unit
    private static final String KEY_RECOVERY_RATE = "recoveryRate"; // Recovery rate
    private static final String KEY_MAX_STAMINA = "maxStamina";     // Max stamina

    private static final String[] COLUMNS_GAMES = {
            KEY_ID,
            KEY_TITLE,
            KEY_IMAGE,
            KEY_CATEGORY,
            KEY_UNIT,
            KEY_RECOVERY_RATE,
            KEY_MAX_STAMINA
    };

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create the table of names
        String CREATE_GAMES_TABLE = "CREATE TABLE " + TABLE_GAMES + " (" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_TITLE + " TEXT, " +
                KEY_IMAGE + " TEXT, " +
                KEY_CATEGORY + " TEXT, " +
                KEY_UNIT + " TEXT, " +
                KEY_RECOVERY_RATE + " INTEGER, " +
                KEY_MAX_STAMINA + " INTEGER ); ";

        // create names table
        db.execSQL(CREATE_GAMES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older names table if it exists
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GAMES);

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

        // Build the NameDetail object
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

        // Return the name detail
        return game;
    }

    // Get the list of all games
    public List<Game> getAllGames() {
        List<Game> gameList = new LinkedList<>();

        String query;

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

        // Return the list of games
        return gameList;
    }

    // Update the game details
    public void updateGame(int id, String mTitle, String mImage, String mCategory,
                           String mUnit, int mRecoveryRate, int mMaxStamina) {
        // Get reference to a writable database
        SQLiteDatabase db = this.getWritableDatabase();

        // Create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, mTitle);
        values.put(KEY_IMAGE, mImage);
        values.put(KEY_CATEGORY, mCategory);
        values.put(KEY_UNIT, mUnit);
        values.put(KEY_RECOVERY_RATE, mRecoveryRate);
        values.put(KEY_MAX_STAMINA, mMaxStamina);

        // Update the row
        db.update(TABLE_GAMES,                      // table
                values,                             // values
                KEY_ID + " = ?",                    // selections
                new String[]{String.valueOf(id)});  // selection arguments

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
}
