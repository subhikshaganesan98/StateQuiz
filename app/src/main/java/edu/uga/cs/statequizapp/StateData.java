package edu.uga.cs.statequizapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class StateData {

    public static final String DEBUG_TAG = "StateData";

    private SQLiteOpenHelper stateDbHelper;
    private SQLiteDatabase db;

    private static final String[] allColumns = {
            StateDBHelper.COLUMN_ID,
            StateDBHelper.COLUMN_NAME,
            StateDBHelper.COLUMN_CAPITAL_CITY,
            StateDBHelper.COLUMN_SECOND_CITY,
            StateDBHelper.COLUMN_THIRD_CITY,
            StateDBHelper.COLUMN_STATEHOOD,
            StateDBHelper.COLUMN_CAPITAL_SINCE,
            StateDBHelper.COLUMN_SIZE_RANK
    };

    public StateData(Context context) {
        this.stateDbHelper = StateDBHelper.getInstance(context);
    }

    // Open the database
    public void open() {
        if (db == null || !db.isOpen()) {
            db = stateDbHelper.getWritableDatabase();
            Log.d(DEBUG_TAG, "StateData: db open");
        }
    }

    // Close the database
    public void close() {
        if (db != null && db.isOpen()) {
            db.close();
            Log.d(DEBUG_TAG, "StateData: db closed");
        }
    }

    public boolean isDBOpen() {
        return db != null && db.isOpen();
    }

    // Store a new state in the database
    public State storeState(State state) {
        open(); // Ensure the database is open
        ContentValues values = new ContentValues();
        values.put(StateDBHelper.COLUMN_NAME, state.getName());
        values.put(StateDBHelper.COLUMN_CAPITAL_CITY, state.getCapitalCity());
        values.put(StateDBHelper.COLUMN_SECOND_CITY, state.getSecondCity());
        values.put(StateDBHelper.COLUMN_THIRD_CITY, state.getThirdCity());
        values.put(StateDBHelper.COLUMN_STATEHOOD, state.getStatehood());
        values.put(StateDBHelper.COLUMN_CAPITAL_SINCE, state.getCapitalSince());
        values.put(StateDBHelper.COLUMN_SIZE_RANK, state.getSizeRank());

        long id = db.insert(StateDBHelper.TABLE_STATES, null, values);
        state.setId(id);

        Log.d(DEBUG_TAG, "Stored new state with id: " + state.getId());
        Log.d(DEBUG_TAG, "Stored new state with id, and capital city: " + state.getCapitalCity());

        return state;
    }

    // Retrieve a specific state by its ID
    public State retrieveStateById(long id) {
        open(); // Ensure the database is open
        Cursor cursor = null;
        State state = null;

        try {
            cursor = db.query(StateDBHelper.TABLE_STATES, allColumns,
                    StateDBHelper.COLUMN_ID + " = ?",
                    new String[]{String.valueOf(id)},
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                state = cursorToState(cursor);
            }
        } catch (Exception e) {
            Log.d(DEBUG_TAG, "Retrieve By State Exception caught: " + e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return state;
    }

    // Helper method to convert a cursor to a State object
    private State cursorToState(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndex(StateDBHelper.COLUMN_ID));
        String name = cursor.getString(cursor.getColumnIndex(StateDBHelper.COLUMN_NAME));
        String capitalCity = cursor.getString(cursor.getColumnIndex(StateDBHelper.COLUMN_CAPITAL_CITY));
        String secondCity = cursor.getString(cursor.getColumnIndex(StateDBHelper.COLUMN_SECOND_CITY));
        String thirdCity = cursor.getString(cursor.getColumnIndex(StateDBHelper.COLUMN_THIRD_CITY));
        int statehood = cursor.getInt(cursor.getColumnIndex(StateDBHelper.COLUMN_STATEHOOD));
        int capitalSince = cursor.getInt(cursor.getColumnIndex(StateDBHelper.COLUMN_CAPITAL_SINCE));
        int sizeRank = cursor.getInt(cursor.getColumnIndex(StateDBHelper.COLUMN_SIZE_RANK));

        State state = new State(name, capitalCity, secondCity, thirdCity,
                statehood, capitalSince, sizeRank);
        state.setId(id);

        return state;
    }

    public List<State> retrieveLast50States() {
        open(); // Ensure the database is open
        ArrayList<State> states = new ArrayList<>();
        Cursor cursor = null;

        try {
            // Adjust the SQL query to retrieve the last 50 rows
            cursor = db.query(
                    StateDBHelper.TABLE_STATES, allColumns,
                    null, null, null, null,
                    StateDBHelper.COLUMN_ID + " DESC", "49"
            );

            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    if (cursor.getColumnCount() >= 8) {
                        states.add(cursorToState(cursor));
                    }
                }
            }
            Log.d(DEBUG_TAG, "retrieveLast50States Number of records from DB: " + cursor.getCount());
        } catch (Exception e) {
            Log.d(DEBUG_TAG, "retrieveLast50States Exception caught: " + e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return states;
    }
}
