package com.example.e_season;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class ApplySeasonActivity extends AppCompatActivity {

    private static final String TAG = "ApplySeasonActivity";

    private Spinner startStationSpinner;
    private Spinner endStationSpinner;
    private EditText seasonStartDateEditText;
    private EditText seasonEndDateEditText;
    private Spinner classSpinner;
    private TextView seasonPriceTextView;
    private Button applySeasonButton;

    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_season);

        try {
            // Initialize Firebase Database and Auth
            databaseReference = FirebaseDatabase.getInstance().getReference("seasons");
            mAuth = FirebaseAuth.getInstance();

            // Initialize views
            startStationSpinner = findViewById(R.id.startStationSpinner);
            endStationSpinner = findViewById(R.id.endStationSpinner);
            seasonStartDateEditText = findViewById(R.id.seasonStartDateEditText);
            seasonEndDateEditText = findViewById(R.id.seasonEndDateEditText);
            classSpinner = findViewById(R.id.classSpinner);
            seasonPriceTextView = findViewById(R.id.seasonPriceTextView);
            applySeasonButton = findViewById(R.id.applySeasonButton);

            // Fetch data from API and populate spinners
            fetchAndPopulateSpinners();

            // Populate class spinner
            populateClassSpinner();

            // Set up date pickers
            seasonStartDateEditText.setOnClickListener(v -> showDatePickerDialog(seasonStartDateEditText));
            seasonEndDateEditText.setOnClickListener(v -> showDatePickerDialog(seasonEndDateEditText));

            // Set up apply season button click listener
            applySeasonButton.setOnClickListener(v -> handleApplySeason());

            // Set up listeners to update the season price
            startStationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    updateSeasonPrice();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            endStationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    updateSeasonPrice();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            classSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    updateSeasonPrice();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            seasonStartDateEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    updateSeasonPrice();
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            seasonEndDateEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    updateSeasonPrice();
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Exception in onCreate", e);
            showError(e.getMessage());
        }
    }

    private void fetchAndPopulateSpinners() {
        new Thread(() -> {
            try {
                List<String> startStations = fetchStations("startStation");
                List<String> endStations = fetchStations("endStation");

                runOnUiThread(() -> {
                    populateSpinner(startStationSpinner, startStations);
                    populateSpinner(endStationSpinner, endStations);
                });
            } catch (IOException e) {
                Log.e(TAG, "Failed to fetch stations", e);
                runOnUiThread(() -> showError("Failed to fetch data: " + e.getMessage()));
            }
        }).start();
    }

    private List<String> fetchStations(String stationType) throws IOException {
        List<String> stations = new ArrayList<>();
        Document doc = Jsoup.connect("https://eservices.railway.gov.lk/schedule/searchTrain.action?lang=en").get();
        Elements elements = doc.select("select#" + stationType + " option");
        for (Element element : elements) {
            String station = element.text();
            if (!station.isEmpty()) {
                stations.add(station);
            }
        }
        return stations;
    }

    private void populateSpinner(Spinner spinner, List<String> items) {
        try {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
        } catch (Exception e) {
            Log.e(TAG, "Exception in populateSpinner", e);
            showError("An error occurred while populating spinner: " + e.getMessage());
        }
    }

    private void populateClassSpinner() {
        try {
            List<String> classes = new ArrayList<>();
            classes.add("1st Class");
            classes.add("2nd Class");
            classes.add("3rd Class");

            populateSpinner(classSpinner, classes);
        } catch (Exception e) {
            Log.e(TAG, "Exception in populateClassSpinner", e);
            showError("An error occurred while populating class spinner: " + e.getMessage());
        }
    }

    private void showDatePickerDialog(final EditText editText) {
        try {
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
        } catch (Exception e) {
            Log.e(TAG, "Exception in showDatePickerDialog", e);
            showError("An error occurred while showing date picker: " + e.getMessage());
        }
    }

    private void updateSeasonPrice() {
        try {
            String startStation = startStationSpinner.getSelectedItem().toString();
            String endStation = endStationSpinner.getSelectedItem().toString();
            String seasonStartDate = seasonStartDateEditText.getText().toString().trim();
            String seasonEndDate = seasonEndDateEditText.getText().toString().trim();
            String selectedClass = classSpinner.getSelectedItem().toString();

            if (startStation.isEmpty() || endStation.isEmpty() || seasonStartDate.isEmpty() || seasonEndDate.isEmpty() || selectedClass.isEmpty()) {
                seasonPriceTextView.setText("LKR 0");
                return;
            }

            int minPrice, maxPrice;

            switch (selectedClass) {
                case "1st Class":
                    minPrice = 1500;
                    maxPrice = 15000;
                    break;
                case "2nd Class":
                    minPrice = 800;
                    maxPrice = 10000;
                    break;
                case "3rd Class":
                    minPrice = 500;
                    maxPrice = 700;
                    break;
                default:
                    minPrice = 0;
                    maxPrice = 0;
                    break;
            }

            if (minPrice != 0 && maxPrice != 0) {
                Random random = new Random();
                int price = random.nextInt(maxPrice - minPrice + 1) + minPrice;
                seasonPriceTextView.setText("LKR " + price);
            } else {
                seasonPriceTextView.setText("LKR 0");
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception in updateSeasonPrice", e);
            showError("An error occurred while updating season price: " + e.getMessage());
        }
    }

    private void handleApplySeason() {
        try {
            String startStation = startStationSpinner.getSelectedItem().toString();
            String endStation = endStationSpinner.getSelectedItem().toString();
            String seasonStartDate = seasonStartDateEditText.getText().toString().trim();
            String seasonEndDate = seasonEndDateEditText.getText().toString().trim();
            String selectedClass = classSpinner.getSelectedItem().toString();
            String seasonPrice = seasonPriceTextView.getText().toString();

            if (seasonStartDate.isEmpty() || seasonEndDate.isEmpty()) {
                showError("Please select both start and end dates");
                return;
            }

            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                String userEmail = currentUser.getEmail();

                // Log the data being passed to PaymentActivity
                Log.d(TAG, "Start Station: " + startStation);
                Log.d(TAG, "End Station: " + endStation);
                Log.d(TAG, "Season Start Date: " + seasonStartDate);
                Log.d(TAG, "Season End Date: " + seasonEndDate);
                Log.d(TAG, "Selected Class: " + selectedClass);
                Log.d(TAG, "Season Price: " + seasonPrice);
                Log.d(TAG, "User Email: " + userEmail);

                // Start PaymentActivity and pass the season data
                Intent intent = new Intent(ApplySeasonActivity.this, PaymentActivity.class);
                intent.putExtra("startStation", startStation);
                intent.putExtra("endStation", endStation);
                intent.putExtra("seasonStartDate", seasonStartDate);
                intent.putExtra("seasonEndDate", seasonEndDate);
                intent.putExtra("selectedClass", selectedClass);
                intent.putExtra("seasonPrice", seasonPrice);
                intent.putExtra("userEmail", userEmail);
                startActivity(intent);
            } else {
                showError("User not logged in");
                Log.e(TAG, "User not logged in");
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception in handleApplySeason", e);
            showError("An error occurred while applying season: " + e.getMessage());
        }
    }

    private void showError(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }
}