package edu.uga.cs.statequizapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class QuizDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "QuizDatabase";
    private static final int DATABASE_VERSION = 1;

    // Define table and column names
    public static final String TABLE_QUESTIONS = "questions";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_STATE_NAME = "state_name";
    public static final String COLUMN_CAPITAL_CITY = "capital_city";
    public static final String COLUMN_SECOND_CITY = "second_city";
    public static final String COLUMN_THIRD_CITY = "third_city";
    public static final String COLUMN_CITY_SIZE_RANK = "city_size_rank";

    // Define the create table statement
    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_QUESTIONS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_STATE_NAME + " TEXT, " +
                    COLUMN_CAPITAL_CITY + " TEXT, " +
                    COLUMN_SECOND_CITY + " TEXT, " +
                    COLUMN_THIRD_CITY + " TEXT, " +
                    COLUMN_CITY_SIZE_RANK + " INTEGER" + ");";

    public QuizDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the questions table
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Perform database upgrade if needed
        // Typically, you would drop the existing tables and create new ones
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTIONS);
        onCreate(db);
    }
}

