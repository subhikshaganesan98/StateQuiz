package edu.uga.cs.statequizapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class QuizDBHelper extends SQLiteOpenHelper {

    public static final String TAG = "QuizDBHelper";
    public static final String DATABASE_NAME = "StateQuiz.db";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_QUIZ = "quiz";
    public static final String QUIZ_COLUMN_ID = "quiz_id";
    public static final String QUIZ_COLUMN_DATE = "quiz_date";
    public static final String QUIZ_COLUMN_TIME = "quiz_time";
    public static final String QUIZ_COLUMN_QUESTION1_ID = "question1_id";
    public static final String QUIZ_COLUMN_QUESTION2_ID = "question2_id";
    public static final String QUIZ_COLUMN_QUESTION3_ID = "question3_id";
    public static final String QUIZ_COLUMN_QUESTION4_ID = "question4_id";
    public static final String QUIZ_COLUMN_QUESTION5_ID = "question5_id";
    public static final String QUIZ_COLUMN_QUESTION6_ID = "question6_id";
    public static final String QUIZ_COLUMN_CORRECT_ANSWERS = "correct_answers";
    public static final String QUIZ_COLUMN_QUESTIONS_ANSWERED = "questions_answered";

    private static QuizDBHelper instance;

    private QuizDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized QuizDBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new QuizDBHelper(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createQuizTableQuery = "CREATE TABLE " + TABLE_QUIZ + " (" +
                QUIZ_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                QUIZ_COLUMN_DATE + " TEXT, " +
                QUIZ_COLUMN_TIME + " TEXT, " +
                QUIZ_COLUMN_QUESTION1_ID + " INTEGER, " +
                QUIZ_COLUMN_QUESTION2_ID + " INTEGER, " +
                QUIZ_COLUMN_QUESTION3_ID + " INTEGER, " +
                QUIZ_COLUMN_QUESTION4_ID + " INTEGER, " +
                QUIZ_COLUMN_QUESTION5_ID + " INTEGER, " +
                QUIZ_COLUMN_QUESTION6_ID + " INTEGER, " +
                QUIZ_COLUMN_CORRECT_ANSWERS + " INTEGER, " +
                QUIZ_COLUMN_QUESTIONS_ANSWERED + " INTEGER, " +
                "FOREIGN KEY (" + QUIZ_COLUMN_QUESTION1_ID + ") REFERENCES " + StateDBHelper.TABLE_STATES + "(" + StateDBHelper.COLUMN_ID + ")," +
                "FOREIGN KEY (" + QUIZ_COLUMN_QUESTION2_ID + ") REFERENCES " + StateDBHelper.TABLE_STATES + "(" + StateDBHelper.COLUMN_ID + ")," +
                "FOREIGN KEY (" + QUIZ_COLUMN_QUESTION3_ID + ") REFERENCES " + StateDBHelper.TABLE_STATES + "(" + StateDBHelper.COLUMN_ID + ")," +
                "FOREIGN KEY (" + QUIZ_COLUMN_QUESTION4_ID + ") REFERENCES " + StateDBHelper.TABLE_STATES + "(" + StateDBHelper.COLUMN_ID + ")," +
                "FOREIGN KEY (" + QUIZ_COLUMN_QUESTION5_ID + ") REFERENCES " + StateDBHelper.TABLE_STATES + "(" + StateDBHelper.COLUMN_ID + ")," +
                "FOREIGN KEY (" + QUIZ_COLUMN_QUESTION6_ID + ") REFERENCES " + StateDBHelper.TABLE_STATES + "(" + StateDBHelper.COLUMN_ID + ")" +
                ")";
        db.execSQL(createQuizTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Implement if needed when upgrading the database
    }

    public long addQuiz(QuizQuestion quiz) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(QUIZ_COLUMN_DATE, quiz.getQuizDate());
        values.put(QUIZ_COLUMN_TIME, quiz.getTime());
        values.put(QUIZ_COLUMN_QUESTION1_ID, quiz.getQuestion1Id());
        values.put(QUIZ_COLUMN_QUESTION2_ID, quiz.getQuestion2Id());
        values.put(QUIZ_COLUMN_QUESTION3_ID, quiz.getQuestion3Id());
        values.put(QUIZ_COLUMN_QUESTION4_ID, quiz.getQuestion4Id());
        values.put(QUIZ_COLUMN_QUESTION5_ID, quiz.getQuestion5Id());
        values.put(QUIZ_COLUMN_QUESTION6_ID, quiz.getQuestion6Id());
        values.put(QUIZ_COLUMN_CORRECT_ANSWERS, quiz.getCorrectAnswers());
        values.put(QUIZ_COLUMN_QUESTIONS_ANSWERED, quiz.getQuestionsAnswered());

        long id = db.insert(TABLE_QUIZ, null, values);
        Log.d(TAG, "Quiz added with id: " + id);
        db.close();
        return id;
    }

    public List<QuizQuestion> getAllQuizzes() {
        List<QuizQuestion> quizzes = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_QUIZ, null, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                QuizQuestion quiz = cursorToQuiz(cursor);
                quizzes.add(quiz);
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return quizzes;
    }

    private QuizQuestion cursorToQuiz(Cursor cursor) {
        return new QuizQuestion(
                cursor.getString(cursor.getColumnIndex(QUIZ_COLUMN_DATE)),
                cursor.getString(cursor.getColumnIndex(QUIZ_COLUMN_TIME)),
                cursor.getInt(cursor.getColumnIndex(QUIZ_COLUMN_QUESTION1_ID)),
                cursor.getInt(cursor.getColumnIndex(QUIZ_COLUMN_QUESTION2_ID)),
                cursor.getInt(cursor.getColumnIndex(QUIZ_COLUMN_QUESTION3_ID)),
                cursor.getInt(cursor.getColumnIndex(QUIZ_COLUMN_QUESTION4_ID)),
                cursor.getInt(cursor.getColumnIndex(QUIZ_COLUMN_QUESTION5_ID)),
                cursor.getInt(cursor.getColumnIndex(QUIZ_COLUMN_QUESTION6_ID)),
                cursor.getInt(cursor.getColumnIndex(QUIZ_COLUMN_CORRECT_ANSWERS)),
                cursor.getInt(cursor.getColumnIndex(QUIZ_COLUMN_QUESTIONS_ANSWERED))
        );
    }
}
