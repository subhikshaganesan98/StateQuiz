package edu.uga.cs.statequizapp;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
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

    private static final String[] stateCapitals = {};
    private StateQuizDBHelper dbHelper;
    private SQLiteDatabase db;

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
        RadioButton capitalCityButton = view.findViewById(R.id.choiceA1);
        RadioButton cityOneButton = view.findViewById(R.id.choiceB1);
        RadioButton cityTwoButton = view.findViewById(R.id.choiceC1);




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
        answerChoices.add("A. " + retrievedState.getCapitalCity());
        answerChoices.add("B. " + retrievedState.getSecondCity());
        answerChoices.add("C. " + retrievedState.getThirdCity());

        // Shuffle the answer choices using java.util.Random
        Collections.shuffle(answerChoices, new Random());

        // Update the frontend of the fragment
        questionView.setText("What is the Capital of " + retrievedState.getName() + "?");
        capitalCityButton.setText(answerChoices.get(0));
        cityOneButton.setText(answerChoices.get(1));
        cityTwoButton.setText(answerChoices.get(2));



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