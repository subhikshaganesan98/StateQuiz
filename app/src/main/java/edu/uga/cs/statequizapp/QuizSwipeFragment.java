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

        stateData = new StateData(this.getActivity());
        stateData.open();
        stateData.storeState(state);
        State newState = stateData.retrieveStateById(state.getId());

        // Create an instance of CsvReadingTask
        ReadCsvAndStoreStateTask readTask = new ReadCsvAndStoreStateTask(stateDetailsList, stateData, this.requireContext());
        readTask.execute();





        // This is where I will read the CSV file and store it in the stateDetailsList
//
//        try {
//            // Open the CSV data file in the assets folder
//            InputStream in_s = getActivity().getAssets().open("StateDetails.csv");
//            Log.d("Opening CSV File Status: ", "This works");
//
//            // Read the CSV data
//            CSVReader reader = new CSVReader(new InputStreamReader(in_s));
//            String[] nextRow;
//
//            try {
//                while ((nextRow = reader.readNext()) != null) {
//                    // Concatenate the values in the row into a single string
//                    String rowAsString = String.join(",", nextRow); // You can use a different delimiter if needed
//                    stateDetailsList.add(rowAsString);
//                }
//            } catch (CsvValidationException e) {
//                e.printStackTrace();
//            }
//
//            // Now, stateDetailsList contains the entire rows from the CSV file as single strings
//
//            // Testing the stateDetailsList by printing the rows:
//            for (String stateDetails : stateDetailsList) {
//                Log.d("Row Value: ", stateDetails);
//            }
//
//        } catch (IOException e) {
//            Log.d("Opening CSV File Status: ", "This does not work");
//            e.printStackTrace();
//        }
//
//        // Remove the first element (index 0) from the list
//        if (!stateDetailsList.isEmpty()) {
//            stateDetailsList.remove(0);
//        }
//
//        // Shuffle the list to randomize the order
//        Collections.shuffle(stateDetailsList);
//
//        // retrieving all values(StateName and cities) from the Row value of stateDetails List index
//        String stateName = "Unknown";
//        String capitalCity = "Unknown";
//        String cityOne = "Unknown";
//        String cityTwo = "Unknown";
//
//        String row = stateDetailsList.get(versionNum);
//
//        // Split the row using a delimiter (e.g., comma)
//        String[] rowValues = row.split(","); // You can use a different delimiter if needed
//
//        // Check if the rowValues array has at least one element
//        if (rowValues.length > 0) {
//            stateName = rowValues[0]; // Access the first value
//            capitalCity = rowValues[1]; // Access the second value
//            cityOne = rowValues[2]; // Access the third value
//            cityTwo = rowValues[3]; // Access the fourth value
//
//            // testing the values now
//            Log.d("State Name: ", stateName);
//            Log.d("Capital City: ", capitalCity);
//            Log.d("City One: ", cityOne);
//            Log.d("City Two: ", cityTwo);
//
//        } else {
//            Log.d("Error: Row is empty", "");
//        }
//
//
        long myStateid = 1323;

        // updating the frontend of the fragment
        questionView.setText("What is the Capital of " + stateData.retrieveStateById(1375).getName() + "?");
        capitalCityButton.setText("A. " + stateData.retrieveStateById(1375).getCapitalCity());
        cityOneButton.setText("B. " + stateData.retrieveStateById(1375).getSecondCity());
        cityTwoButton.setText("C. " + stateData.retrieveStateById(1375).getThirdCity());



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

                while ((nextRow = reader.readNext()) != null) {
                    // Parse the data and create a State object
                    State state = new State("Name", "Capital", "Second City", "Third City", 0, 0, 0);
                    state.setName(nextRow[0]);
                    state.setCapitalCity(nextRow[1]);
                    state.setSecondCity(nextRow[2]);
                    state.setThirdCity(nextRow[3]);


                    try {
                        // Check if the strings can be parsed to integers
                        state.setStatehood(Integer.parseInt(nextRow[4]));
                        state.setCapitalSince(Integer.parseInt(nextRow[5]));
                        state.setSizeRank(Integer.parseInt(nextRow[6]));
                    } catch (NumberFormatException e) {
                        // Handle the case where parsing to integer fails
                        Log.e("ReadCsvAndStoreStateTask", "Error parsing integers: " + e.getMessage());
                        continue; // Skip this row and proceed to the next one
                    }

                    states.add(state);
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

        // No need for onPostExecute if you're not updating a UI component immediately
    }

}