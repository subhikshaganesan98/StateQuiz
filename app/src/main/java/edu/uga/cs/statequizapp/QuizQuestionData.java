package edu.uga.cs.statequizapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class QuizQuestionData {

    // Database fields
    private SQLiteDatabase database;
    private QuizQuestionDBHelper dbHelper;

    public QuizQuestionData(Context context) {
        dbHelper = new QuizQuestionDBHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    // Add a new quiz question to the database
    public long addQuizQuestion(QuizQuestion quizQuestion) {
        ContentValues values = new ContentValues();
        values.put(QuizQuestionDBHelper.COLUMN_QUIZ_DATE, quizQuestion.getQuizDate());
        values.put(QuizQuestionDBHelper.COLUMN_TIME, quizQuestion.getTime());
        values.put(QuizQuestionDBHelper.COLUMN_QUESTION_1_ID, quizQuestion.getQuestion1Id());
        values.put(QuizQuestionDBHelper.COLUMN_QUESTION_2_ID, quizQuestion.getQuestion2Id());
        values.put(QuizQuestionDBHelper.COLUMN_QUESTION_3_ID, quizQuestion.getQuestion3Id());
        values.put(QuizQuestionDBHelper.COLUMN_QUESTION_4_ID, quizQuestion.getQuestion4Id());
        values.put(QuizQuestionDBHelper.COLUMN_QUESTION_5_ID, quizQuestion.getQuestion5Id());
        values.put(QuizQuestionDBHelper.COLUMN_QUESTION_6_ID, quizQuestion.getQuestion6Id());
        values.put(QuizQuestionDBHelper.COLUMN_CORRECT_ANSWERS, quizQuestion.getCorrectAnswers());
        values.put(QuizQuestionDBHelper.COLUMN_QUESTIONS_ANSWERED, quizQuestion.getQuestionsAnswered());

        // Insert the new row, returning the primary key value of the new row
        long insertId = database.insert(QuizQuestionDBHelper.TABLE_QUIZ_QUESTION, null, values);
        Log.d("QuizQuestionData", "Inserted quiz question with ID: " + insertId);
        return insertId;
    }

    // Get a list of all quiz questions in the database
    public List<QuizQuestion> getAllQuizQuestions() {
        List<QuizQuestion> quizQuestions = new ArrayList<>();

        Cursor cursor = database.query(QuizQuestionDBHelper.TABLE_QUIZ_QUESTION,
                null, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            QuizQuestion quizQuestion = cursorToQuizQuestion(cursor);
            quizQuestions.add(quizQuestion);
            cursor.moveToNext();
        }
        cursor.close();

        return quizQuestions;
    }

    // Convert a database cursor to a QuizQuestion object
    private QuizQuestion cursorToQuizQuestion(Cursor cursor) {
        QuizQuestion quizQuestion = new QuizQuestion(
                cursor.getString(cursor.getColumnIndex(QuizQuestionDBHelper.COLUMN_QUIZ_DATE)),
                cursor.getString(cursor.getColumnIndex(QuizQuestionDBHelper.COLUMN_TIME)),
                cursor.getInt(cursor.getColumnIndex(QuizQuestionDBHelper.COLUMN_QUESTION_1_ID)),
                cursor.getInt(cursor.getColumnIndex(QuizQuestionDBHelper.COLUMN_QUESTION_2_ID)),
                cursor.getInt(cursor.getColumnIndex(QuizQuestionDBHelper.COLUMN_QUESTION_3_ID)),
                cursor.getInt(cursor.getColumnIndex(QuizQuestionDBHelper.COLUMN_QUESTION_4_ID)),
                cursor.getInt(cursor.getColumnIndex(QuizQuestionDBHelper.COLUMN_QUESTION_5_ID)),
                cursor.getInt(cursor.getColumnIndex(QuizQuestionDBHelper.COLUMN_QUESTION_6_ID)),
                cursor.getInt(cursor.getColumnIndex(QuizQuestionDBHelper.COLUMN_CORRECT_ANSWERS)),
                cursor.getInt(cursor.getColumnIndex(QuizQuestionDBHelper.COLUMN_QUESTIONS_ANSWERED))
        );

        return quizQuestion;
    }
}
