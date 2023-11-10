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


    // which Android version to display in the fragment
    private int versionNum;

    public QuizSwipeFragment() {
        // Required empty public constructor
    }

    public static QuizSwipeFragment newInstance( int versionNum ) {
        QuizSwipeFragment fragment = new QuizSwipeFragment();
        Bundle args = new Bundle();
        args.putInt( "versionNum", versionNum );
        fragment.setArguments( args );
        return fragment;
    }

    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        if( getArguments() != null ) {
            versionNum = getArguments().getInt( "versionNum" );
        }
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_quiz_swipe, container, false );
    }

    public int getQuizScore() {
        return this.versionNum;
    }

    @Override
    public void onViewCreated( @NonNull View view, Bundle savedInstanceState ) {
        //public void onActivityCreated(Bundle savedInstanceState) {
        super.onViewCreated( view, savedInstanceState );

        TextView questionView = view.findViewById( R.id.questionView );

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


        // Set up the OnCheckedChangeListener for the RadioGroup for First Set of Answer Choices
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // Handle checked change events here
                Log.d("CheckedRadioButtonId: ", "" + checkedId);

                // Now, you can check the selected RadioButton based on its ID
                if (checkedId == capitalCityButton.getId()) {
                    Log.d("Selected Button: ", "Choice One");
                    if(retrievedState.getCapitalCity().equals(firstChoice)) {
                        Log.d("Correct: ", "choice One");
                    } else {
                        Log.d("Incorrect: ", "choice One");
                    }
                    // Handle logic for the capital city radio button
                } else if (checkedId == cityOneButton.getId()) {
                    Log.d("Selected Button: ", "Choice Two");
                    if(retrievedState.getCapitalCity().equals(secondChoice)) {
                        Log.d("Correct: ", "choice two");
                    } else {
                        Log.d("Incorrect: ", "choice two");
                    }
                    // Handle logic for the city one radio button
                } else if (checkedId == cityTwoButton.getId()) {
                    Log.d("Selected Button: ", "Choice Three");
                    if(retrievedState.getCapitalCity().equals(thirdChoice)) {
                        Log.d("Correct: ", "choice three");
                    } else {
                        Log.d("Incorrect: ", "choice three");
                    }
                    // Handle logic for the city two radio button
                }
            }
        });

        // Set up the OnCheckedChangeListener for the second RadioGroup
        radioGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // Handle checked change events here for the second RadioGroup
                Log.d("CheckedRadioButtonId (Group 2): ", "" + checkedId);

                // Now, you can check the selected RadioButton based on its ID
                if (checkedId == yesButton.getId()) {
                    Log.d("Selected Button (Group 2): ", "Yes");
                    if (retrievedState.getSizeRank() == 1) {
                        Log.d("Correct: ", "yes");
                    } else {
                        Log.d("Incorrect: ", "yes");
                    }
                    // Handle logic for the Yes radio button
                } else if (checkedId == noButton.getId()) {
                    Log.d("Selected Button (Group 2): ", "No");
                    if (retrievedState.getSizeRank() > 1) {
                        Log.d("Correct: ", "no");
                    } else {
                        Log.d("Incorrect: ", "no");
                    }
                    // Handle logic for the No radio button
                }
            }
        });





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

            stateData.close();

            return null;
        }

        // Might need to do onPostExecute() to update UI elements asyncronously
    }



}