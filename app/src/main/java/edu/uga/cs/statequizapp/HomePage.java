package edu.uga.cs.statequizapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class HomePage extends Fragment {

    private QuizQuestionData quizQuestionData;
    private QuizQuestion quizQuestion;

    public HomePage() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_page, container, false);

        Button beginQuizButton = view.findViewById(R.id.beginQuizButton);
        Button aboutButton = view.findViewById(R.id.aboutButton);
        Button highScoreButton = view.findViewById(R.id.highScoreButton);

        beginQuizButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Replace the current fragment with the QuizFragment
                QuizFragment quizFragment = new QuizFragment();
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, quizFragment);
                transaction.addToBackStack(null);
                transaction.commit();

                quizQuestionData = new QuizQuestionData(view.getContext());
                quizQuestionData.open();

                // setting the current time
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                TimeZone easternTimeZone = TimeZone.getTimeZone("America/New_York");
                timeFormat.setTimeZone(easternTimeZone);
                String formattedTime = timeFormat.format(new Date()); // Format the current date and time as a string

                // setting the current date
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // Example format
                String currentDate = dateFormat.format(new Date());

                quizQuestion = new QuizQuestion(currentDate, formattedTime, 0, 0, 0, 0,0,0, 0, 0);
                quizQuestionData.addQuizQuestion(quizQuestion);



                // Perform the database update here
                quizQuestion.setCorrectAnswers(
                        quizQuestion.getQuestion1Id() + quizQuestion.getQuestion2Id() +  quizQuestion.getQuestion3Id() +
                                quizQuestion.getQuestion4Id() + quizQuestion.getQuestion5Id() + quizQuestion.getQuestion6Id()
                );


                QuizQuestion retrievedQuestion = quizQuestion;


                quizQuestionData.updateQuizQuestion(quizQuestion, quizQuestion.getId());
                quizQuestion = quizQuestionData.getLatestQuizQuestion();

                Log.d("Correct Answers: ", "" + quizQuestion.getCorrectAnswers());
                Log.d("Correct Date: ", "" + quizQuestion.getQuizDate() );
                Log.d("Correct Time: ", "" + quizQuestion.getTime() );


                quizQuestionData.close();

            }
        });

        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Replace the current fragment with the AboutGameFragment
                AboutGameFragment aboutFragment = new AboutGameFragment();
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, aboutFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        highScoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Replace the current fragment with the QuizResultFragment
                QuizResultFragment resultFragment = new QuizResultFragment();
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, resultFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return view;
    }
}
