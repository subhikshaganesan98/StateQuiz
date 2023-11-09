package edu.uga.cs.statequizapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StateDBHelper extends SQLiteOpenHelper {

    public static final String TAG = "StateDBHelper";
    public static final String DATABASE_NAME = "StateQuiz.db";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_STATES = "states";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_CAPITAL_CITY = "capital_city";
    public static final String COLUMN_SECOND_CITY = "second_city";
    public static final String COLUMN_THIRD_CITY = "third_city";
    public static final String COLUMN_STATEHOOD = "statehood";
    public static final String COLUMN_CAPITAL_SINCE = "capital_since";
    public static final String COLUMN_SIZE_RANK = "size_rank";

    public long lastRandomStateId = -1;

    // Singleton instance
    private static StateDBHelper instance;

    private StateDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized StateDBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new StateDBHelper(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the states table
        String createTableQuery = "CREATE TABLE " + TABLE_STATES + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_CAPITAL_CITY + " TEXT, " +
                COLUMN_SECOND_CITY + " TEXT, " +
                COLUMN_THIRD_CITY + " TEXT, " +
                COLUMN_STATEHOOD + " INTEGER, " +
                COLUMN_CAPITAL_SINCE + " INTEGER, " +
                COLUMN_SIZE_RANK + " INTEGER)";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Implement if needed when upgrading the database
    }

    // Add a new state to the database
    public long addState(State state) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, state.getName());
        values.put(COLUMN_CAPITAL_CITY, state.getCapitalCity());
        values.put(COLUMN_SECOND_CITY, state.getSecondCity());
        values.put(COLUMN_THIRD_CITY, state.getThirdCity());
        values.put(COLUMN_STATEHOOD, state.getStatehood());
        values.put(COLUMN_CAPITAL_SINCE, state.getCapitalSince());
        values.put(COLUMN_SIZE_RANK, state.getSizeRank());

        long id = db.insert(TABLE_STATES, null, values);
        Log.d(TAG, "State added with id: " + id);
        db.close();
        return id;
    }

    // Get a random state from the database
    public State getRandomState() {
        SQLiteDatabase db = getReadableDatabase();

        // Select a random state excluding the last retrieved state
        String selection = COLUMN_ID + " != ?";
        String[] selectionArgs = {String.valueOf(lastRandomStateId)};

        Cursor cursor = db.query(TABLE_STATES, null, selection, selectionArgs, null, null, "RANDOM()", "1");

        State state = null;
        if (cursor != null && cursor.moveToFirst()) {
            state = cursorToState(cursor);
            lastRandomStateId = state.getId();
        }

        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return state;
    }

    // Get a list of all states in the database
    public List<State> getAllStates() {
        List<State> states = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_STATES, null, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                State state = cursorToState(cursor);
                states.add(state);
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return states;
    }

    // Helper method to convert cursor to State object
    private State cursorToState(Cursor cursor) {
        return new State(
                cursor.getString(cursor.getColumnIndex(COLUMN_NAME)),
                cursor.getString(cursor.getColumnIndex(COLUMN_CAPITAL_CITY)),
                cursor.getString(cursor.getColumnIndex(COLUMN_SECOND_CITY)),
                cursor.getString(cursor.getColumnIndex(COLUMN_THIRD_CITY)),
                cursor.getInt(cursor.getColumnIndex(COLUMN_STATEHOOD)),
                cursor.getInt(cursor.getColumnIndex(COLUMN_CAPITAL_SINCE)),
                cursor.getInt(cursor.getColumnIndex(COLUMN_SIZE_RANK))
        );
    }
}
