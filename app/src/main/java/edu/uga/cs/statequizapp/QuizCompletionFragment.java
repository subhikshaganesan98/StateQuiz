package edu.uga.cs.statequizapp;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class QuizCompletionFragment extends Fragment {

    private QuizQuestionData quizQuestionData;

    public QuizCompletionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_quiz_completion, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        //public void onActivityCreated(Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // get references of UI elements
        TextView textScore = view.findViewById(R.id.textViewScore);
        TextView textQuestionsAnswered = view.findViewById(R.id.textViewQuestionsAnswered);
        TextView textTime = view.findViewById(R.id.textViewTime);
        TextView textDate = view.findViewById(R.id.textViewDate);



        // Get the activity context
        Context context = getActivity();

        // retrieve and update our final results from quiz question data:
        this.quizQuestionData = new QuizQuestionData(context);
        quizQuestionData.open();

        QuizQuestion retrievedQuestion = quizQuestionData.getLatestQuizQuestion();


        // setting the current time
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        TimeZone easternTimeZone = TimeZone.getTimeZone("America/New_York");
        timeFormat.setTimeZone(easternTimeZone);
        String formattedTime = timeFormat.format(new Date()); // Format the current date and time as a string
        retrievedQuestion.setTime(formattedTime);

        // setting the current date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // Example format
        String currentDate = dateFormat.format(new Date());
        retrievedQuestion.setQuizDate(currentDate);

        quizQuestionData.updateQuizQuestion(retrievedQuestion, retrievedQuestion.getId());
        retrievedQuestion = quizQuestionData.getLatestQuizQuestion();

        quizQuestionData.close();

        // update the UI elements
        textScore.setText("Score: " + retrievedQuestion.getCorrectAnswers() + "/12");
        textQuestionsAnswered.setText("Questions Answered: " + retrievedQuestion.getQuestionsAnswered() + "/12");
        textDate.setText("Date: " + retrievedQuestion.getQuizDate());
        textTime.setText("Time: " + retrievedQuestion.getTime());


        Log.d("My Quiz Score: ", "" +  retrievedQuestion.getQuestion1Id() + ", " + retrievedQuestion.getQuestion2Id() + ", " +
                retrievedQuestion.getQuestion3Id() + ", " + retrievedQuestion.getQuestion4Id() + ", " +
                retrievedQuestion.getQuestion5Id() + ", " + retrievedQuestion.getQuestion6Id());


    }

    // I want to change score textView when resumes
    @Override
    public void onResume() {
        super.onResume();

        // Open the database
        if( quizQuestionData != null && !quizQuestionData.isDBOpen() ) {
            quizQuestionData.open();
            Log.d( "QuizCompletion", "QuizCompletion.onResume(): opening DB" );
        }

        // Get references to the UI elements
        TextView textScore = requireView().findViewById(R.id.textViewScore);

        QuizQuestion retrievedQuestion = quizQuestionData.getLatestQuizQuestion();

        // Update the score TextView with the updated score
        textScore.setText("Score: " + quizQuestionData.getLatestQuizQuestion().getCorrectAnswers() + "/12");

        Log.d("My Resumed Quiz Score: ", "" +  retrievedQuestion.getQuestion1Id() + ", " + retrievedQuestion.getQuestion2Id() + ", " +
                retrievedQuestion.getQuestion3Id() + ", " + retrievedQuestion.getQuestion4Id() + ", " +
                retrievedQuestion.getQuestion5Id() + ", " + retrievedQuestion.getQuestion6Id());

    }




    @Override
    public void onPause() {
        super.onPause();

        if (quizQuestionData != null) {
            quizQuestionData.close(); // Close the quiz question database connection
            quizQuestionData = null; // Set the reference to null to avoid reusing a closed database
        }
    }



}
