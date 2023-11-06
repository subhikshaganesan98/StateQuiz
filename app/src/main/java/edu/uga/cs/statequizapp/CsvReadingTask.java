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
            Log.d("CSV File Status: ", "Opened successfully");

            // Read the CSV data
            CSVReader reader = new CSVReader(new InputStreamReader(in_s));
            String[] nextRow;

            while ((nextRow = reader.readNext()) != null) {
                // Parse the data and create a State object
                State state = new State();
                state.setStateName(nextRow[0]);
                state.setCapitalCity(nextRow[1]);
                state.setSecondCity(nextRow[2]);
                state.setThirdCity(nextRow[3]);
                state.setStatehoodYear(Integer.parseInt(nextRow[4]));
                state.setCapitalSince(nextRow[5]);
                state.setSizeRank(Integer.parseInt(nextRow[6]));

                stateList.add(state);
            }
        } catch (IOException e) {
            Log.e("CSV File Status: ", "Failed to open or read CSV file");
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
            Log.d("State Name: ", state.getStateName());
            Log.d("Capital City: ", state.getCapitalCity());
            Log.d("Statehood Year: ", String.valueOf(state.getStatehoodYear()));
            Log.d("Second City: ", state.getSecondCity());
            Log.d("Third City: ", state.getThirdCity());
            Log.d("Capital Since: ", state.getCapitalSince());
            Log.d("Size Rank: ", String.valueOf(state.getSizeRank()));
        }


    }
}
