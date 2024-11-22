package com.example.e_season;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.e_season.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ApplySeasonActivity extends AppCompatActivity {

    private Spinner startStationSpinner;
    private Spinner endStationSpinner;
    private EditText seasonStartDateEditText;
    private EditText seasonEndDateEditText;
    private Spinner classSpinner;
    private Button applySeasonButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_season);

        // Initialize views
        startStationSpinner = findViewById(R.id.startStationSpinner);
        endStationSpinner = findViewById(R.id.endStationSpinner);
        seasonStartDateEditText = findViewById(R.id.seasonStartDateEditText);
        seasonEndDateEditText = findViewById(R.id.seasonEndDateEditText);
        classSpinner = findViewById(R.id.classSpinner);
        applySeasonButton = findViewById(R.id.applySeasonButton);

        // Fetch data from API and populate spinners
        fetchAndPopulateSpinners();

        // Set up date pickers
        seasonStartDateEditText.setOnClickListener(v -> showDatePickerDialog(seasonStartDateEditText));
        seasonEndDateEditText.setOnClickListener(v -> showDatePickerDialog(seasonEndDateEditText));

        // Set up apply season button click listener
        applySeasonButton.setOnClickListener(v -> handleApplySeason());
    }

    private void fetchAndPopulateSpinners() {
        // Simulate fetching data from an API
        List<String> startStations = new ArrayList<>();
        startStations.add("Station 1");
        startStations.add("Station 2");
        startStations.add("Station 3");

        List<String> endStations = new ArrayList<>();
        endStations.add("Station A");
        endStations.add("Station B");
        endStations.add("Station C");

        List<String> classes = new ArrayList<>();
        classes.add("First Class");
        classes.add("Second Class");
        classes.add("Third Class");

        // Populate spinners
        populateSpinner(startStationSpinner, startStations);
        populateSpinner(endStationSpinner, endStations);
        populateSpinner(classSpinner, classes);
    }

    private void populateSpinner(Spinner spinner, List<String> items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void showDatePickerDialog(final EditText editText) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1;
                    editText.setText(date);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void handleApplySeason() {
        String startStation = startStationSpinner.getSelectedItem().toString();
        String endStation = endStationSpinner.getSelectedItem().toString();
        String seasonStartDate = seasonStartDateEditText.getText().toString().trim();
        String seasonEndDate = seasonEndDateEditText.getText().toString().trim();
        String selectedClass = classSpinner.getSelectedItem().toString();

        if (seasonStartDate.isEmpty() || seasonEndDate.isEmpty()) {
            Toast.makeText(this, "Please select both start and end dates", Toast.LENGTH_SHORT).show();
            return;
        }

        // Perform apply season logic here
        // For example, send the data to the server or save it in the database

        Toast.makeText(this, "Season applied successfully", Toast.LENGTH_SHORT).show();
    }
}