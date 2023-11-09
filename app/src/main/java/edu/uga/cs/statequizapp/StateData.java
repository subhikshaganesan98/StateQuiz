package edu.uga.cs.statequizapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class StateData {

    public static final String DEBUG_TAG = "StateData";

    private SQLiteDatabase db;
    private SQLiteOpenHelper stateDbHelper;

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



    public List<State> retrieveAllStates() {
        ArrayList<State> states = new ArrayList<>();
        Cursor cursor = null;
        int columnIndex;

        try {
            cursor = db.query(StateDBHelper.TABLE_STATES, allColumns,
                    null, null, null, null, null);

            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    if (cursor.getColumnCount() >= 8) {
                        columnIndex = cursor.getColumnIndex(StateDBHelper.COLUMN_ID);
                        long id = cursor.getLong(columnIndex);
                        columnIndex = cursor.getColumnIndex(StateDBHelper.COLUMN_NAME);
                        String name = cursor.getString(columnIndex);
                        columnIndex = cursor.getColumnIndex(StateDBHelper.COLUMN_CAPITAL_CITY);
                        String capitalCity = cursor.getString(columnIndex);
                        columnIndex = cursor.getColumnIndex(StateDBHelper.COLUMN_SECOND_CITY);
                        String secondCity = cursor.getString(columnIndex);
                        columnIndex = cursor.getColumnIndex(StateDBHelper.COLUMN_THIRD_CITY);
                        String thirdCity = cursor.getString(columnIndex);
                        columnIndex = cursor.getColumnIndex(StateDBHelper.COLUMN_STATEHOOD);
                        int statehood = cursor.getInt(columnIndex);
                        columnIndex = cursor.getColumnIndex(StateDBHelper.COLUMN_CAPITAL_SINCE);
                        int capitalSince = cursor.getInt(columnIndex);
                        columnIndex = cursor.getColumnIndex(StateDBHelper.COLUMN_SIZE_RANK);
                        int sizeRank = cursor.getInt(columnIndex);

                        State state = new State(name, capitalCity, secondCity, thirdCity,
                                statehood, capitalSince, sizeRank);
                        state.setId(id);
                        states.add(state);
                        Log.d(DEBUG_TAG, "Retrieved State: " + state);

                    }
                }
            }
            if (cursor != null)
                Log.d(DEBUG_TAG, "Number of records from DB: " + cursor.getCount());
            else
                Log.d(DEBUG_TAG, "Number of records from DB: 0");
        } catch (Exception e) {
            Log.d(DEBUG_TAG, "Exception caught: " + e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return states;
    }

    // Open the database
    public void open() {
        db = stateDbHelper.getWritableDatabase();
        Log.d(DEBUG_TAG, "StateData: db open");
    }

    // Close the database
    public void close() {
        if (stateDbHelper != null) {
            stateDbHelper.close();
            Log.d(DEBUG_TAG, "StateData: db closed");
        }
    }

    public boolean isDBOpen() {
        return db.isOpen();
    }

    // Store a new state in the database
    public State storeState(State state) {
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

    // Update an existing state in the database
    public void updateState(State state) {
        ContentValues values = new ContentValues();
        values.put(StateDBHelper.COLUMN_NAME, state.getName());
        values.put(StateDBHelper.COLUMN_CAPITAL_CITY, state.getCapitalCity());
        values.put(StateDBHelper.COLUMN_SECOND_CITY, state.getSecondCity());
        values.put(StateDBHelper.COLUMN_THIRD_CITY, state.getThirdCity());
        values.put(StateDBHelper.COLUMN_STATEHOOD, state.getStatehood());
        values.put(StateDBHelper.COLUMN_CAPITAL_SINCE, state.getCapitalSince());
        values.put(StateDBHelper.COLUMN_SIZE_RANK, state.getSizeRank());

        db.update(StateDBHelper.TABLE_STATES, values,
                StateDBHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(state.getId())});

        Log.d(DEBUG_TAG, "Updated state with id: " + state.getId());
    }

    // This method will delete the DB or a specific row
    public void deleteState(State state) {
        SQLiteDatabase db = stateDbHelper.getWritableDatabase();

        if (state == null) {
            // Delete all states
            db.delete(StateDBHelper.TABLE_STATES, null, null);
            Log.d(DEBUG_TAG, "All states deleted from the database.");
        } else {
            // Delete a specific state
            long id = state.getId();
            db.delete(StateDBHelper.TABLE_STATES,
                    StateDBHelper.COLUMN_ID + " = ?",
                    new String[]{String.valueOf(id)});
            Log.d(DEBUG_TAG, "Deleted state with id: " + id);
        }

        db.close();
    }

    // Retrieve a specific state by its ID
    public State retrieveStateById(long id) {
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
            Log.d(DEBUG_TAG, "Exception caught: " + e);
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

    public List<State> getRandomQuizStates() {
        List<State> selectedStates = new ArrayList<>();
        SQLiteDatabase db = stateDbHelper.getReadableDatabase();

        // Get the total number of states in the database
        long numOfStates = DatabaseUtils.queryNumEntries(db, StateDBHelper.TABLE_STATES);

        // Choose to retrieve the last 50 states or all states if less than 50
        long startIndex = Math.max(0, numOfStates - 50);

        // Use java.util.Random to generate random indices
        Random random = new Random();

        Set<Integer> selectedIndices = new HashSet<>(); // Use HashSet to ensure uniqueness

        while (selectedIndices.size() < 6 && startIndex < numOfStates) {
            int randomIndex = random.nextInt((int) numOfStates);

            // Ensure the index is within the selected range and is unique
            if (randomIndex >= startIndex && selectedIndices.add(randomIndex)) {
                Cursor cursor = db.query(
                        StateDBHelper.TABLE_STATES,
                        allColumns,
                        StateDBHelper.COLUMN_ID + " = ?",
                        new String[]{String.valueOf(randomIndex)},
                        null, null, null);

                if (cursor != null && cursor.moveToFirst()) {
                    State state = cursorToState(cursor);
                    selectedStates.add(state);
                }

                if (cursor != null) {
                    cursor.close();
                }
            }
        }

        // Close the database
        db.close();

        return selectedStates;
    }


}
