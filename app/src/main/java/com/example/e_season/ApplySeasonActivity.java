package com.example.e_season;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
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
            Toast.makeText(this, "An error occurred during initialization", Toast.LENGTH_SHORT).show();
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
                runOnUiThread(() ->
                        Toast.makeText(ApplySeasonActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show()
                );
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
            Toast.makeText(this, "An error occurred while populating spinner", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, "An error occurred while populating class spinner", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, "An error occurred while showing date picker", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, "An error occurred while updating season price", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(this, "Please select both start and end dates", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                String userEmail = currentUser.getEmail();

                // Create a new season entry
                String seasonId = databaseReference.push().getKey();
                if (seasonId == null) {
                    Toast.makeText(this, "Failed to generate season ID", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Failed to generate season ID");
                    return;
                }

                Season season = new Season(startStation, endStation, seasonStartDate, seasonEndDate, selectedClass, userEmail, seasonPrice);

                // Save the season entry to Firebase
                databaseReference.child(seasonId).setValue(season)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Season applied successfully", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Season applied successfully");
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Failed to apply season", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Failed to apply season", e);
                        });
            } else {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "User not logged in");
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception in handleApplySeason", e);
            Toast.makeText(this, "An error occurred while applying season", Toast.LENGTH_SHORT).show();
        }
    }

    // Season class to represent the data structure
    public static class Season {
        public String startStation;
        public String endStation;
        public String seasonStartDate;
        public String seasonEndDate;
        public String selectedClass;
        public String userEmail;
        public String seasonPrice;

        public Season() {
            // Default constructor required for calls to DataSnapshot.getValue(Season.class)
        }

        public Season(String startStation, String endStation, String seasonStartDate, String seasonEndDate, String selectedClass, String userEmail, String seasonPrice) {
            this.startStation = startStation;
            this.endStation = endStation;
            this.seasonStartDate = seasonStartDate;
            this.seasonEndDate = seasonEndDate;
            this.selectedClass = selectedClass;
            this.userEmail = userEmail;
            this.seasonPrice = seasonPrice;
        }
    }
}