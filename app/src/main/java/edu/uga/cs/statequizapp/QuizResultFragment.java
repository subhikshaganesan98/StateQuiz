package edu.uga.cs.statequizapp;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.List;

public class QuizResultFragment extends Fragment {

    private List<QuizQuestion> quizQuestionList;
    private TextView textQuizResults;

    private QuizQuestionData quizQuestionData;

    public QuizResultFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_quiz_result, container, false);

        textQuizResults = view.findViewById(R.id.textQuizResults);

        // Get the activity context
        Context context = getActivity();




        // Execute AsyncTask to retrieve quiz questions
        new RetrievePastResultsTask(context, this.quizQuestionList).execute();




        // Return the inflated view
        return view;
    }

    // I want to change score textView when resumes
    @Override
    public void onResume() {
        super.onResume();

        if(quizQuestionList != null)
            new RetrievePastResultsTask(this.requireContext(), this.quizQuestionList).execute();

    }

    @Override
    public void onPause() {
        super.onPause();

        if (quizQuestionData != null) {
            quizQuestionData.close(); // Close the quiz question database connection
            quizQuestionData = null; // Set the reference to null to avoid reusing a closed database
        }
    }

    // Method to update the UI with the results
    private void updateResultsList(List<QuizQuestion> results) {
        StringBuilder sb = new StringBuilder();
//        QuizQuestion retrievedQuestion = quizQuestionData.getLatestQuizQuestion();
//
//
//
//        sb.append("Score: ").append(retrievedQuestion.getCorrectAnswers())
//                .append(", Time: ").append(retrievedQuestion.getTime())
//                .append(", Date: ").append(retrievedQuestion.getQuizDate())
//                .append("\n\n"); // Adding a line break for separation


//        for(long i = retrievedQuestion.getId(); i > 0; i--) {
//            sb.append("Score: ").append(retrievedQuestion.getCorrectAnswers())
//                    .append(", Time: ").append(retrievedQuestion.getTime())
//                    .append(", Date: ").append(retrievedQuestion.getQuizDate())
//                    .append("\n\n"); // Adding a line break for separation
//
//        }

        for (QuizQuestion question : results) {
            sb.append("Score: ").append(question.getCorrectAnswers())
                    .append(", Time: ").append(question.getTime())
                    .append(", Date: ").append(question.getQuizDate())
                    .append("\n\n"); // Adding a line break for separation
        }

        // Split the string into lines and reverse the order of lines
        String[] lines = sb.toString().split("\n\n");
        sb.setLength(0); // Clear the StringBuilder
        for (int i = lines.length - 1; i >= 0; i--) {
            sb.append(lines[i]).append("\n\n");
        }

        Log.d("results Fragement String:", sb.toString());
        textQuizResults.setText(sb.toString());
    }




    // Definition of the AsyncTask as an inner class of QuizResultFragment
    private class RetrievePastResultsTask extends AsyncTask<Void, List<QuizQuestion>> {
        private Context context;
        private List<QuizQuestion> quizQuestions;



        // Constructor accepts a Context
        public RetrievePastResultsTask(Context context, List<QuizQuestion> quizQuestions) {
            this.context = context;
            this.quizQuestions = quizQuestions;
        }

        // This method will run on a background thread
        @Override
        protected List<QuizQuestion> doInBackground(Void... voids) {
            quizQuestionData = new QuizQuestionData(context);
            quizQuestionData.open();
            List<QuizQuestion> results = quizQuestionData.getAllQuizQuestions(); // my method
            quizQuestionData.close();
            return results;
        }

        // This method will run on the UI thread after the background computation finishes
        @Override
        protected void onPostExecute(List<QuizQuestion> results) {
            // Update UI with the list of results, this method runs on the main thread
            updateResultsList(results);
        }
    }



}
