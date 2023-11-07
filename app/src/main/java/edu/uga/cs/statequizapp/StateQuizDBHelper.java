package edu.uga.cs.statequizapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class StateQuizDBHelper extends SQLiteOpenHelper {

    private Context context;
    private static final String DEBUG_TAG = "StateQuizDBHelper";

    private static final String DB_NAME = "statequiz.db";
    private static final int DB_VERSION = 1;

    // Define table and column names for your state quiz data.
    public static final String TABLE_STATES = "states";
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
            "CREATE TABLE " + TABLE_STATES + " ("
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
        this.context = context;
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


    public void closeDB(SQLiteDatabase db) {
        if (db != null && db.isOpen()) {
            db.close();
        }
    }

    void addState(String stateName, String capital, String secondCity, String thridCity,
                  int statehoodYear, String capitalSince, int sizeRank) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(STATE_COLUMN_NAME, stateName);
        cv.put(STATE_COLUMN_CAPITAL, stateName);
        cv.put(STATE_COLUMN_SECOND_CITY, stateName);
        cv.put(STATE_COLUMN_THIRD_CITY, stateName);
        cv.put(STATE_COLUMN_STATEHOOD_YEAR, stateName);
        cv.put(STATE_COLUMN_CAPITAL_SINCE, stateName);
        cv.put(STATE_COLUMN_SIZE_RANK, stateName);

        long result = db.insert(TABLE_STATES, null, cv);

        if(result == -1) {
            Toast.makeText(this.context, "Failed", Toast.LENGTH_SHORT).show();
            Log.d("Inserting a State: ", "Failed");
        } else {
            Toast.makeText(this.context, "Added Sucessfully!", Toast.LENGTH_SHORT).show();
            Log.d("Inserting a State: ", "Added Sucessfully!");
        }



    }

    public State getStateById(long stateId) {
        SQLiteDatabase db = this.getReadableDatabase();
        State state = null;

        String query = "SELECT * FROM " + TABLE_STATES + " WHERE " + STATE_COLUMN_NAME + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(stateId)});

        if (cursor.moveToFirst()) {
            state = new State();
            state.setId(cursor.getLong(cursor.getColumnIndex(STATE_COLUMN_NAME)));
            state.setStateName(cursor.getString(cursor.getColumnIndex(STATE_COLUMN_NAME)));
            state.setCapitalCity(cursor.getString(cursor.getColumnIndex(STATE_COLUMN_CAPITAL)));
            // Add more properties as needed
        }

        cursor.close();
        db.close();

        return state;
    }
}