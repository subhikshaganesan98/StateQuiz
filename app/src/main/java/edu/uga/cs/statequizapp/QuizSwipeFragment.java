package edu.uga.cs.statequizapp;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.util.Log;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import android.os.AsyncTask;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link QuizSwipeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QuizSwipeFragment extends Fragment {


    // Create a list to store the entire rows from the CSV file as single strings
    private List<State> stateDetailsList = new ArrayList<>();

    private StateData stateData;

    private long lastStateIndex = 0;

    // Declare a variable to store the total quiz score
    private int totalQuizScore = 0;

    private int questionScore = 0;
    private int groupOneScore = 0;
    private int groupTwoScore = 0;
    private int totalGroupScore = 0;

    private boolean checkGroup1 = false;
    private boolean checkGroup2 = false;

    private QuizQuestionData quizQuestionData;
    private QuizQuestion retrievedQuestion;

    // flag variable for selecting wrong choices in group 2 consequtively
    private boolean checkConsequtiveIncorrect = false;

    private int questionScore2 = 0;


    // instance variables to handle the questionsAnswered
    private int groupOneSelection = 0;
    private int groupTwoSelection = 0;
    private boolean groupOneAnswered = false;
    private boolean groupTwoAnswered = false;



    // which Android version to display in the fragment
    private int versionNum;

    public QuizSwipeFragment() {
        // Required empty public constructor
    }

    public static QuizSwipeFragment newInstance(int versionNum) {
        QuizSwipeFragment fragment = new QuizSwipeFragment();
        Bundle args = new Bundle();
        args.putInt("versionNum", versionNum);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            versionNum = getArguments().getInt("versionNum");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_quiz_swipe, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        //public void onActivityCreated(Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView questionView = view.findViewById(R.id.questionView);

        // first set of answer choices
        RadioButton capitalCityButton = view.findViewById(R.id.choiceA1);
        RadioButton cityOneButton = view.findViewById(R.id.choiceB1);
        RadioButton cityTwoButton = view.findViewById(R.id.choiceC1);
        RadioGroup radioGroup = view.findViewById(R.id.answerChoices1);

        // second set of answer choices
        RadioGroup radioGroup2 = view.findViewById(R.id.answerChoices2);
        RadioButton yesButton = view.findViewById(R.id.choiceA2);
        RadioButton noButton = view.findViewById(R.id.choiceB2);


        State state = new State("DefaultName", "DefaultCapital", "DefaultSecondCity",
                "DefaultThirdCity", 0, 0, 0);

        // setting and opening up the DB
        stateData = new StateData(this.getActivity());
        stateData.open();

        // Create an instance of CsvReadingTask
        ReadCsvAndStoreStateTask readTask = new ReadCsvAndStoreStateTask(stateDetailsList, stateData, this.requireContext());
        readTask.execute();

        // randomize the list of 50(last 50 rows) from DB
        List<State> randomizedStates = stateData.retrieveLast50States();
        Collections.shuffle(randomizedStates, new Random());

        State retrievedState = randomizedStates.get(versionNum);


        // Get the answer choices
        List<String> answerChoices = new ArrayList<>();
        answerChoices.add(retrievedState.getCapitalCity());
        answerChoices.add(retrievedState.getSecondCity());
        answerChoices.add(retrievedState.getThirdCity());

        // Shuffle the answer choices using java.util.Random
        Collections.shuffle(answerChoices, new Random());

        // saving the choices:
        String firstChoice = answerChoices.get(0);
        String secondChoice = answerChoices.get(1);
        String thirdChoice = answerChoices.get(2);

        // Update the frontend of the fragment
        questionView.setText("What is the Capital of " + retrievedState.getName() + "?");
        capitalCityButton.setText("A: " + firstChoice);
        cityOneButton.setText("B: " + secondChoice);
        cityTwoButton.setText("C: " + thirdChoice);

        // This part will deal with QuizQuestionDB
        this.quizQuestionData = new QuizQuestionData(this.getActivity());
        quizQuestionData.open();

        this.retrievedQuestion = quizQuestionData.getLatestQuizQuestion();
        Log.d("QuizQuestionID: ", "" + retrievedQuestion.getId());

        if (savedInstanceState != null) {
            this.totalGroupScore = savedInstanceState.getInt("totalGroupScore", 0);
            this.checkGroup1 = savedInstanceState.getBoolean("checkGroup1", false);
            this.checkGroup2 = savedInstanceState.getBoolean("checkGroup2", false);
            // Restore other relevant state data here
        }


        // radio buttons async tasks
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                String selectedChoice = "";

                // Handle checked change events here
                Log.d("CheckedRadioButtonId: ", "" + checkedId);

                if (checkedId == capitalCityButton.getId()) {
                    selectedChoice = firstChoice;
                    groupOneSelection++;
                } else if (checkedId == cityOneButton.getId()) {
                    selectedChoice = secondChoice;
                    groupOneSelection++;
                } else if (checkedId == cityTwoButton.getId()) {
                    selectedChoice = thirdChoice;
                    groupOneSelection++;
                }

                if (!selectedChoice.isEmpty()) {
                    Log.d("Selected Button: ", selectedChoice + ", " + retrievedState.getName());

                    String correctChoice = retrievedState.getCapitalCity();

                    Log.d("Correct Choice Group 1:", correctChoice);

                    if (correctChoice.equals(selectedChoice)) {
                        Log.d("Correct: ", selectedChoice + ", " + retrievedState.getName());
                        checkGroup1 = true;
                        if (totalGroupScore < 2) {
                            totalGroupScore++;
                        }

                        // We know that user is not selecting 2 incorrect choices consequtively
                        checkConsequtiveIncorrect = false;
                    } else {
                        Log.d("Incorrect: ", selectedChoice + ", " + retrievedState.getName());

                        if (!checkConsequtiveIncorrect){
                            // If the second group is correct and first is wrong, the first group should not decrease
                            // the totalGroupSCore to 0
                            if (totalGroupScore > 0) {
                                if ((checkGroup2 && totalGroupScore > 1) || !checkGroup2)
                                    totalGroupScore--;
                            }
                        }
                        checkConsequtiveIncorrect = true;
                    }
                }

                Log.d("TotalGroupScore: ", "" + totalGroupScore);
                // handling the update the  UpdateQuizScoresTask quizQuestionScore Async Task
                new UpdateQuizScoresTask().execute(versionNum, totalGroupScore, retrievedQuestion, quizQuestionData);


            }
        });


        radioGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                boolean checkCorrect = false;
                boolean checkIncorrect = false;
                String selectedChoice = "";

                // Handle checked change events here for the second RadioGroup
                Log.d("CheckedRadioButtonId (Group 2): ", "" + checkedId);

                if (checkedId == yesButton.getId()) {
                    selectedChoice = "Yes";
                    groupTwoSelection++;
                } else if (checkedId == noButton.getId()) {
                    selectedChoice = "No";
                    groupTwoSelection++;
                }

                if (!selectedChoice.isEmpty()) {
                    Log.d("Selected Button (Group 2): ", selectedChoice + ", " + retrievedState.getName());

                    String correctChoice = (checkedId == yesButton.getId()) ? "Yes" : "No";

                    if ((checkedId == yesButton.getId() && retrievedState.getSizeRank() == 1) ||
                            (checkedId == noButton.getId() && retrievedState.getSizeRank() > 1)) {
                        Log.d("Correct: ", selectedChoice + ", " + retrievedState.getName());
                        checkGroup2 = true;
                        if (totalGroupScore < 2) {
                            totalGroupScore++;
                        }
                    } else {
                        Log.d("Incorrect: ", selectedChoice + ", " + retrievedState.getName());
                        checkIncorrect = true;

                        // If the first group is correct, the second group should not decrease the totalGroupSCore to 0
                        if (totalGroupScore > 0) {
                            if((checkGroup1 && totalGroupScore > 1) || !checkGroup1 )
                                totalGroupScore--;
                        }
                    }
                }

                Log.d("TotalGroupScore: ", "" + totalGroupScore);
                // handling the update the  UpdateQuizScoresTask quizQuestionScore Async Task
                new UpdateQuizScoresTask().execute(versionNum, totalGroupScore, retrievedQuestion, quizQuestionData);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        //this.onViewCreated(requireView(), null);
        // Open the database
        if( quizQuestionData != null && !quizQuestionData.isDBOpen() ) {
            quizQuestionData.open();
            Log.d( TAG, "QuizSwipeFragment.onResume(): opening Quiz Questions DB" );
        }

        if( stateData != null && !stateData.isDBOpen() ) {
            stateData.open();
            Log.d( TAG, "QuizSwipeFragment.onResume(): opening State DB" );
        }

        this.retrievedQuestion = this.quizQuestionData.getLatestQuizQuestion();
        // handling the update the  UpdateQuizScoresTask quizQuestionScore Async Task
        new UpdateQuizScoresTask().execute(
                this.versionNum, this.totalGroupScore,
                this.retrievedQuestion, this.quizQuestionData);
        Log.d("Resumed Fragment Score", "" + retrievedQuestion.getCorrectAnswers());

    }

    @Override
    public void onPause(){
        super.onPause();
        this.retrievedQuestion = quizQuestionData.getLatestQuizQuestion();
        Log.d("Paused Fragment Score", "" + retrievedQuestion.getCorrectAnswers());

        if (stateData != null) {
            stateData.close(); // Close the database connection
            //stateData = null; // Set the reference to null to avoid reusing a closed database
        }

        if (quizQuestionData != null) {
            quizQuestionData.close(); // Close the quiz question database connection
            //quizQuestionData = null; // Set the reference to null to avoid reusing a closed database
        }

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the current state data
        outState.putInt("totalGroupScore", this.totalGroupScore);
        outState.putBoolean("checkGroup1", this.checkGroup1);
        outState.putBoolean("checkGroup2", this.checkGroup2);
        // Save other relevant state data here
    }


    private class UpdateQuizScoresTask extends AsyncTask<Object, Void, Void> {
        @Override
        protected Void doInBackground(Object... params) {
            int versionNum = (int) params[0];
            int totalGroupScore = (int) params[1];
            QuizQuestion retrievedQuestion = (QuizQuestion) params[2];
            QuizQuestionData quizQuestionData = (QuizQuestionData) params[3];

            // update the number of questions selection for group One
            if(groupOneSelection > 0 && !groupOneAnswered) {
                retrievedQuestion.setQuestionsAnswered( retrievedQuestion.getQuestionsAnswered() + 1 );
                groupOneAnswered = true;
                Log.d("Quiz Questions Answered", "" + retrievedQuestion.getQuestionsAnswered());
            }

            // update the number of questions selection for Group Two
            if(groupTwoSelection > 0 && !groupTwoAnswered) {
                retrievedQuestion.setQuestionsAnswered( retrievedQuestion.getQuestionsAnswered() + 1 );
                groupTwoAnswered = true;
                Log.d("Quiz Questions Answered", "" + retrievedQuestion.getQuestionsAnswered());
            }

            Log.d("UpdateQuizScoresTask", "Version number: " + versionNum);
            Log.d("UpdateQuizScoresTask", "Total Group Score: " + totalGroupScore);

            if (versionNum >= 0 && versionNum <= 5) {
                int questionId = versionNum + 1;
                switch (questionId) {
                    case 1:
                        retrievedQuestion.setQuestion1Id(totalGroupScore);
                        break;
                    case 2:
                        retrievedQuestion.setQuestion2Id(totalGroupScore);
                        break;
                    case 3:
                        retrievedQuestion.setQuestion3Id(totalGroupScore);
                        break;
                    case 4:
                        retrievedQuestion.setQuestion4Id(totalGroupScore);
                        break;
                    case 5:
                        retrievedQuestion.setQuestion5Id(totalGroupScore);
                        break;
                    case 6:
                        retrievedQuestion.setQuestion6Id(totalGroupScore);
                        break;
                }

                Log.d("UpdateQuizScoresTask", "Updating score for question " + questionId);

                // Perform the database update here
                retrievedQuestion.setCorrectAnswers(
                        retrievedQuestion.getQuestion1Id() + retrievedQuestion.getQuestion2Id() +  retrievedQuestion.getQuestion3Id() +
                                retrievedQuestion.getQuestion4Id() + retrievedQuestion.getQuestion5Id() + retrievedQuestion.getQuestion6Id()
                );
                quizQuestionData.updateQuizQuestion(retrievedQuestion, retrievedQuestion.getId());

                Log.d("UpdateQuizScoresTask", "Database update performed for question " + questionId);
            } else {
                Log.d("UpdateQuizScoresTask", "Invalid version number: " + versionNum);
            }

            //quizQuestionData.close();
            return null;
        }
    }






    // This will set the total number of Screens to swipe
    public static int getNumberOfVersions() {
        return 6;
    }


    // This is an AsyncTask class (it extends AsyncTask) to perform reading from CSV and DB writing of states, asynchronously.
    public class ReadCsvAndStoreStateTask extends AsyncTask<Void, Void, Void> {

        private List<State> states; // Replace with your ArrayList instance
        private StateData stateData; // Replace with your StateData class instance
        private Context context; // Replace with your Context instance

        public ReadCsvAndStoreStateTask(List<State> states, StateData stateData, Context context) {
            this.states = states;
            this.stateData = stateData;
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                // Open the CSV file from the assets folder
                InputStream in_s = context.getAssets().open("StateDetails.csv");

                // opening the DB
                //SQLiteDatabase db = this.stateData.open();

                // Read the CSV data
                CSVReader reader = new CSVReader(new InputStreamReader(in_s));
                String[] nextRow;

                // skipping the first row
                reader.readNext();

                while ((nextRow = reader.readNext()) != null) {
                    // Parse the data and create a State object
                    State state = new State("Name", "Capital", "Second City", "Third City", 0, 0, 0);
                    state.setName(nextRow[0]);
                    state.setCapitalCity(nextRow[1]);
                    state.setSecondCity(nextRow[2]);
                    state.setThirdCity(nextRow[3]);
                    state.setStatehood(Integer.parseInt(nextRow[4]));
                    state.setCapitalSince(Integer.parseInt(nextRow[5]));
                    state.setSizeRank(Integer.parseInt(nextRow[6]));



                    states.add(state);
                    Log.d("Not Retrieved DB State statehood: ", String.valueOf(state.getStatehood()));
                    stateData.storeState(state);

                    // Retrieve the ID of the state
                    long stateId = state.getId();
                    lastStateIndex = stateId;

                    // Use the ID to retrieve the state from the database
                    State retrievedState = stateData.retrieveStateById(stateId);

                    // Log the details of the retrieved state
                    if (retrievedState != null) {
                        Log.d("DB State id: ", String.valueOf(retrievedState.getId()));
                        Log.d("DB State name: ", retrievedState.getName());
                        Log.d("DB State capital city: ", retrievedState.getCapitalCity());
                        Log.d("DB State second city: ", retrievedState.getSecondCity());
                        Log.d("DB State third city: ", retrievedState.getThirdCity());
                        Log.d("DB State statehood: ", String.valueOf(retrievedState.getStatehood()));
                        Log.d("DB State capital since: ", String.valueOf(retrievedState.getCapitalSince()));
                        Log.d("DB State size rank: ", String.valueOf(retrievedState.getSizeRank()));
                    } else {
                        Log.d("DB State id: ", "State not found");
                    }

                }
            } catch (IOException e) {
                Log.e("ReadCsvAndStoreStateTask", "Failed to open or read CSV file: " + e.getMessage());
                e.printStackTrace();
            } catch (CsvValidationException e) {
                Log.e("ReadCsvAndStoreStateTask", "CSV validation error: " + e.getMessage());
                e.printStackTrace();
            }

            //stateData.close();

            return null;
        }


    }


}