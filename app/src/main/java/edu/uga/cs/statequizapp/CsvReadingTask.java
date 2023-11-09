package edu.uga.cs.statequizapp;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CsvReadingTask extends AsyncTask<Void, Void, List<State>> {
    private Context context;

    public CsvReadingTask(Context context) {
        this.context = context;
    }

    @Override
    protected List<State> doInBackground(Void... voids) {
        List<State> stateList = new ArrayList<>();

        try {
            // Open the CSV file from the assets folder
            InputStream in_s = context.getAssets().open("StateDetails.csv");
            Log.d("My CSV File Status: ", "Opened successfully");

            // Read the CSV data
            CSVReader reader = new CSVReader(new InputStreamReader(in_s));
            String[] nextRow;

            // Create or get an instance of StateDBHelper
            StateDBHelper stateDBHelper = StateDBHelper.getInstance(context);
            StateData stateData = new StateData(context);
            stateData.open();  // Open the database for writing

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

                // Insert the State object into the database
                long insertedId = stateDBHelper.addState(state);
                if (insertedId != -1) {
                    Log.d("DB State Inserted:", state.getName() + ", ID: " + insertedId);
                } else {
                    Log.e("DB Insert Error:", "Failed to insert state: " + state.getName());
                }

                stateList.add(state);
            }

            stateDBHelper.close();  // Close the database after writing
        } catch (IOException e) {
            Log.e("My Status: ", "Failed to open or read CSV file");
            e.printStackTrace();
        } catch (CsvValidationException e) {
            e.printStackTrace();
        }

        return stateList;
    }

    @Override
    protected void onPostExecute(List<State> stateList) {
        // Use the list of State objects in your Fragment or Activity
        for (State state : stateList) {
            Log.d("State Name: ", state.getName());
            Log.d("Capital City: ", state.getCapitalCity());
            Log.d("Statehood Year: ", String.valueOf(state.getStatehood()));
            Log.d("Second City: ", state.getSecondCity());
            Log.d("Third City: ", state.getThirdCity());
            Log.d("Capital Since: ", String.valueOf(state.getCapitalSince()));
            Log.d("Size Rank: ", String.valueOf(state.getSizeRank()));
        }
    }
}
