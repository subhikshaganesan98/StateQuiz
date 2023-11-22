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

    public boolean isDBOpen() {
        return database.isOpen();
    }

    public void close() {
        if(dbHelper != null && this.isDBOpen())
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

    public QuizQuestion getLatestQuizQuestion() {
        QuizQuestion latestQuizQuestion = null;
        Cursor cursor = null;
        int columnIndex;

        try {
            // Adjust the SQL query to retrieve the last quiz question for a specific version
            cursor = database.query(
                    QuizQuestionDBHelper.TABLE_QUIZ_QUESTION, null,
                    null, null, null, null,
                    QuizQuestionDBHelper.COLUMN_ID + " DESC", "1"
            );

            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    if (cursor.getColumnCount() >= 9) {
                        columnIndex = cursor.getColumnIndex(QuizQuestionDBHelper.COLUMN_ID);
                        long id = cursor.getLong(columnIndex);
                        columnIndex = cursor.getColumnIndex(QuizQuestionDBHelper.COLUMN_QUIZ_DATE);
                        String quizDate = cursor.getString(columnIndex);
                        columnIndex = cursor.getColumnIndex(QuizQuestionDBHelper.COLUMN_TIME);
                        String time = cursor.getString(columnIndex);
                        columnIndex = cursor.getColumnIndex(QuizQuestionDBHelper.COLUMN_QUESTION_1_ID);
                        int question1Id = cursor.getInt(columnIndex);
                        // Repeat the above lines for other columns (question2Id, question3Id, ..., questionsAnswered)
                        columnIndex = cursor.getColumnIndex(QuizQuestionDBHelper.COLUMN_QUESTION_2_ID);
                        int question2Id = cursor.getInt(columnIndex);
                        columnIndex = cursor.getColumnIndex(QuizQuestionDBHelper.COLUMN_QUESTION_3_ID);
                        int question3Id = cursor.getInt(columnIndex);
                        columnIndex = cursor.getColumnIndex(QuizQuestionDBHelper.COLUMN_QUESTION_4_ID);
                        int question4Id = cursor.getInt(columnIndex);
                        columnIndex = cursor.getColumnIndex(QuizQuestionDBHelper.COLUMN_QUESTION_5_ID);
                        int question5Id = cursor.getInt(columnIndex);
                        columnIndex = cursor.getColumnIndex(QuizQuestionDBHelper.COLUMN_QUESTION_6_ID);
                        int question6Id = cursor.getInt(columnIndex);
                        columnIndex = cursor.getColumnIndex(QuizQuestionDBHelper.COLUMN_CORRECT_ANSWERS);
                        int correctAnswers = cursor.getInt(columnIndex);
                        columnIndex = cursor.getColumnIndex(QuizQuestionDBHelper.COLUMN_QUESTIONS_ANSWERED);
                        int questionsAnswered = cursor.getInt(columnIndex);

                        latestQuizQuestion = new QuizQuestion(
                                quizDate, time, question1Id, question2Id, question3Id,
                                question4Id, question5Id, question6Id, correctAnswers, questionsAnswered
                        );
                        latestQuizQuestion.setId(id);
                        Log.d("QuizQuestionData", "Retrieved Latest QuizQuestion: " + latestQuizQuestion);
                    }
                }
            }
            if (cursor != null)
                Log.d("QuizQuestionData", "Number of records from DB: " + cursor.getCount());
            else
                Log.d("QuizQuestionData", "Number of records from DB: 0");
        } catch (Exception e) {
            Log.d("QuizQuestionData", "Exception caught: " + e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return latestQuizQuestion;
    }




    // Update an existing quiz question in the database with new question IDs
    public int updateQuizQuestion(QuizQuestion quizQuestion, long questionId) {
        ContentValues values = new ContentValues();
        values.put(QuizQuestionDBHelper.COLUMN_QUESTION_1_ID, quizQuestion.getQuestion1Id());
        values.put(QuizQuestionDBHelper.COLUMN_QUESTION_2_ID, quizQuestion.getQuestion2Id());
        values.put(QuizQuestionDBHelper.COLUMN_QUESTION_3_ID, quizQuestion.getQuestion3Id());
        values.put(QuizQuestionDBHelper.COLUMN_QUESTION_4_ID, quizQuestion.getQuestion4Id());
        values.put(QuizQuestionDBHelper.COLUMN_QUESTION_5_ID, quizQuestion.getQuestion5Id());
        values.put(QuizQuestionDBHelper.COLUMN_QUESTION_6_ID, quizQuestion.getQuestion6Id());

        values.put(QuizQuestionDBHelper.COLUMN_CORRECT_ANSWERS, quizQuestion.getCorrectAnswers());
        values.put(QuizQuestionDBHelper.COLUMN_QUESTIONS_ANSWERED, quizQuestion.getQuestionsAnswered());

        String selection = QuizQuestionDBHelper.COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(quizQuestion.getId())};

        // Update the row, returning the number of rows affected
        return database.update(QuizQuestionDBHelper.TABLE_QUIZ_QUESTION, values, selection, selectionArgs);
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

//    // Method to retrieve the last 10 quiz questions as a List
//    public List<QuizQuestion> getLastTenQuizQuestions() {
//        List<QuizQuestion> quizQuestions = new ArrayList<>();
//
//        // Open the database for reading
//        SQLiteDatabase db = dbHelper.getReadableDatabase();
//        QuizQuestion retrievedQuestion = this.getLatestQuizQuestion();
//        for(int i = 0; i < 10; i++) {
//
//            quizQuestions(1) = retrievedQuestion;
//            retrievedQuestion = this.getAllQuizQuestions();
//        }
//        db.close();
//
//        return quizQuestions;
//    }
}
