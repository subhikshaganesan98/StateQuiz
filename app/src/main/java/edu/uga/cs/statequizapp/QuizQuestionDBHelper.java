package edu.uga.cs.statequizapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class QuizQuestionDBHelper extends SQLiteOpenHelper {

    // Database information
    private static final String DATABASE_NAME = "quiz_questions.db";
    private static final int DATABASE_VERSION = 1;

    // Table name and columns
    public static final String TABLE_QUIZ_QUESTION = "quiz_questions";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_QUIZ_DATE = "quiz_date";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_QUESTION_1_ID = "question1_id";
    public static final String COLUMN_QUESTION_2_ID = "question2_id";
    public static final String COLUMN_QUESTION_3_ID = "question3_id";
    public static final String COLUMN_QUESTION_4_ID = "question4_id";
    public static final String COLUMN_QUESTION_5_ID = "question5_id";
    public static final String COLUMN_QUESTION_6_ID = "question6_id";
    public static final String COLUMN_CORRECT_ANSWERS = "correct_answers";
    public static final String COLUMN_QUESTIONS_ANSWERED = "questions_answered";

    // Table creation SQL statement
    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_QUIZ_QUESTION + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_QUIZ_DATE + " TEXT, " +
                    COLUMN_TIME + " TEXT, " +
                    COLUMN_QUESTION_1_ID + " INTEGER, " +
                    COLUMN_QUESTION_2_ID + " INTEGER, " +
                    COLUMN_QUESTION_3_ID + " INTEGER, " +
                    COLUMN_QUESTION_4_ID + " INTEGER, " +
                    COLUMN_QUESTION_5_ID + " INTEGER, " +
                    COLUMN_QUESTION_6_ID + " INTEGER, " +
                    COLUMN_CORRECT_ANSWERS + " INTEGER, " +
                    COLUMN_QUESTIONS_ANSWERED + " INTEGER);";

    public QuizQuestionDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
        Log.d("QuizQuestionDBHelper", "Created database with table " + TABLE_QUIZ_QUESTION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop the old table if it exists
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUIZ_QUESTION);

        // Create a new table
        onCreate(db);
    }
}
