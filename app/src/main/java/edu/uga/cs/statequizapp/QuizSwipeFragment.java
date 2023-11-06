package edu.uga.cs.statequizapp;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.content.res.Resources;
import android.util.Log;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link QuizSwipeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QuizSwipeFragment extends Fragment {

    private static final String[] stateCapitals = {};




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


        // Create a list to store the entire rows from the CSV file as single strings
        List<String> stateDetailsList = new ArrayList<>();

// This is where I will read the CSV file and store it in the stateDetailsList

        try {
            // Open the CSV data file in the assets folder
            InputStream in_s = getActivity().getAssets().open("StateDetails.csv");
            Log.d("Opening CSV File Status: ", "This works");

            // Read the CSV data
            CSVReader reader = new CSVReader(new InputStreamReader(in_s));
            String[] nextRow;

            try {
                while ((nextRow = reader.readNext()) != null) {
                    // Concatenate the values in the row into a single string
                    String rowAsString = String.join(",", nextRow); // You can use a different delimiter if needed
                    stateDetailsList.add(rowAsString);
                }
            } catch (CsvValidationException e) {
                e.printStackTrace();
            }

            // Now, stateDetailsList contains the entire rows from the CSV file as single strings

            // Testing the stateDetailsList by printing the rows:
            for (String stateDetails : stateDetailsList) {
                Log.d("Row Value: ", stateDetails);
            }

        } catch (IOException e) {
            Log.d("Opening CSV File Status: ", "This does not work");
            e.printStackTrace();
        }

        // Remove the first element (index 0) from the list
        if (!stateDetailsList.isEmpty()) {
            stateDetailsList.remove(0);
        }

        // Shuffle the list to randomize the order
        Collections.shuffle(stateDetailsList);

        // retrieving all values(StateName and cities) from the Row value of stateDetails List index
        String stateName = "Unknown";
        String capitalCity = "Unknown";
        String cityOne = "Unknown";
        String cityTwo = "Unknown";

        String row = stateDetailsList.get(versionNum);

        // Split the row using a delimiter (e.g., comma)
        String[] rowValues = row.split(","); // You can use a different delimiter if needed

        // Check if the rowValues array has at least one element
        if (rowValues.length > 0) {
            stateName = rowValues[0]; // Access the first value
            capitalCity = rowValues[1]; // Access the second value
            cityOne = rowValues[2]; // Access the third value
            cityTwo = rowValues[3]; // Access the fourth value

            // testing the values now
            Log.d("State Name: ", stateName);
            Log.d("Capital City: ", capitalCity);
            Log.d("City One: ", cityOne);
            Log.d("City Two: ", cityTwo);

        } else {
            Log.d("Error: Row is empty", "");
        }


        // updating the frontend of the fragment
        questionView.setText("What is the Capital of " + stateName + "?");
        capitalCityButton.setText("A. " + capitalCity);
        cityOneButton.setText("B. " +cityOne);
        cityTwoButton.setText("C. " +cityTwo);



    }

    // This will set the total number of Screens to swipe
    public static int getNumberOfVersions() {
        return 6;
    }
}