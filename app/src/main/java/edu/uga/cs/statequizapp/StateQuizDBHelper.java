package edu.uga.cs.statequizapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class StateQuizDBHelper extends SQLiteOpenHelper {

    private static final String DEBUG_TAG = "StateQuizDBHelper";

    private static final String DB_NAME = "statequiz.db";
    private static final int DB_VERSION = 1;

    // Define table and column names for your state quiz data.
    public static final String TABLE_STATES = "states";
    public static final String STATE_COLUMN_ID = "_id";
    public static final String STATE_COLUMN_NAME = "state_name";
    public static final String STATE_COLUMN_CAPITAL = "capital_city";
    public static final String STATE_COLUMN_SECOND_CITY = "second_city";
    public static final String STATE_COLUMN_THIRD_CITY = "third_city";
    public static final String STATE_COLUMN_STATEHOOD_YEAR = "statehood_year";
    public static final String STATE_COLUMN_CAPITAL_SINCE = "capital_since";
    public static final String STATE_COLUMN_SIZE_RANK = "size_rank";

    // Reference to the single instance of the helper.
    private static StateQuizDBHelper helperInstance;

    // SQL statement to create the states table.
    private static final String CREATE_STATES =
            "create table " + TABLE_STATES + " ("
                    + STATE_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + STATE_COLUMN_NAME + " TEXT, "
                    + STATE_COLUMN_CAPITAL + " TEXT, "
                    + STATE_COLUMN_SECOND_CITY + " TEXT, "
                    + STATE_COLUMN_THIRD_CITY + " TEXT, "
                    + STATE_COLUMN_STATEHOOD_YEAR + " INTEGER, "
                    + STATE_COLUMN_CAPITAL_SINCE + " TEXT, "
                    + STATE_COLUMN_SIZE_RANK + " INTEGER"
                    + ")";

    private StateQuizDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static synchronized StateQuizDBHelper getInstance(Context context) {
        if (helperInstance == null) {
            helperInstance = new StateQuizDBHelper(context.getApplicationContext());
        }
        return helperInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_STATES);
        Log.d(DEBUG_TAG, "Table " + TABLE_STATES + " created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_STATES);
        onCreate(db);
        Log.d(DEBUG_TAG, "Table " + TABLE_STATES + " upgraded");
    }

    public SQLiteDatabase openWritableDB() {
        return this.getWritableDatabase();
    }

    public SQLiteDatabase openReadableDB() {
        return this.getReadableDatabase();
    }

    public void closeDB(SQLiteDatabase db) {
        if (db != null && db.isOpen()) {
            db.close();
        }
    }

    public State getStateById(long stateId) {
        SQLiteDatabase db = this.getReadableDatabase();
        State state = null;

        String query = "SELECT * FROM " + TABLE_STATES + " WHERE " + STATE_COLUMN_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(stateId)});

        if (cursor.moveToFirst()) {
            state = state = new State("Name", "Capital City", "Second City", "Third City", 2022, 2022, 1);
            state.setId(cursor.getLong(cursor.getColumnIndex(STATE_COLUMN_ID)));
            state.setName(cursor.getString(cursor.getColumnIndex(STATE_COLUMN_NAME)));
            state.setCapitalCity(cursor.getString(cursor.getColumnIndex(STATE_COLUMN_CAPITAL)));
            // Add more properties as needed
        }

        cursor.close();
        db.close();

        return state;
    }
}