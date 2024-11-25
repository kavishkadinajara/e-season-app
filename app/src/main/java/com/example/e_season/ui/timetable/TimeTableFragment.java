package com.example.e_season.ui.timetable;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.e_season.R;
import com.example.e_season.databinding.FragmentTimetableBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TimeTableFragment extends Fragment {

    private static final String TAG = "TimeTableFragment";

    private FragmentTimetableBinding binding;
    private TimeTableAdapter adapter;
    private TimeTableViewModel timeTableViewModel;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        timeTableViewModel = new ViewModelProvider(this).get(TimeTableViewModel.class);

        binding = FragmentTimetableBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        databaseReference = FirebaseDatabase.getInstance().getReference("timetable");
        auth = FirebaseAuth.getInstance();

        try {
            // Initialize RecyclerView
            binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new TimeTableAdapter(new ArrayList<>()); // Pass an empty list initially
            binding.recyclerView.setAdapter(adapter);

            // Observe the timetable data
            timeTableViewModel.getTimeTables().observe(getViewLifecycleOwner(), timeTables -> {
                if (timeTables != null) {
                    Log.d(TAG, "TimeTables updated: " + timeTables.size());
                    adapter.setTimeTableList(timeTables);
                } else {
                    Log.d(TAG, "TimeTables is null");
                }
            });

            // Fetch data from API and populate spinners
            fetchAndPopulateSpinners();

            // Set search button click listener
            binding.searchButton.setOnClickListener(v -> fetchTimeTable());

            // Set reset button click listener
            binding.resetButton.setOnClickListener(v -> resetFields());

            // Set date picker
            binding.searchDateEditText.setOnClickListener(v -> showDatePicker());

            // Fetch timetable data from Firebase
            fetchTimeTableFromFirebase();
        } catch (Exception e) {
            e.printStackTrace();
            showErrorSnackbar("An error occurred during initialization: " + e.getMessage());
        }

        return root;
    }

    private void fetchAndPopulateSpinners() {
        try {
            TimeTableScraper scraper = new TimeTableScraper();
            new Thread(() -> {
                try {
                    List<String> startStations = scraper.getStartStations();
                    List<String> endStations = scraper.getEndStations();

                    getActivity().runOnUiThread(() -> {
                        populateSpinner(binding.startStationSpinner, startStations);
                        populateSpinner(binding.endStationSpinner, endStations);
                        showSuccessSnackbar("Fetched start and end stations successfully.");
                    });
                } catch (IOException e) {
                    getActivity().runOnUiThread(() -> {
                        showErrorSnackbar("Failed to fetch data: " + e.getMessage());
                    });
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
            showErrorSnackbar("An error occurred while fetching and populating spinners: " + e.getMessage());
        }
    }

    private void fetchTimeTable() {
        try {
            final String startStation = binding.startStationSpinner.getSelectedItem().toString();
            final String endStation = binding.endStationSpinner.getSelectedItem().toString();

            TimeTableScraper scraper = new TimeTableScraper();
            new Thread(() -> {
                try {
                    List<TimeTable> timeTableList = scraper.getTimeTable(startStation, endStation);
                    Log.d(TAG, "Fetched TimeTables: " + timeTableList.size());
                    getActivity().runOnUiThread(() -> {
                        timeTableViewModel.setTimeTableList(timeTableList);
                        if (timeTableList.isEmpty()) {
                            showErrorSnackbar("No timetable data found.");
                        } else {
                            showSuccessSnackbar("Fetched timetable data successfully.");
                            saveTimeTableToFirebase(timeTableList);
                        }
                    });
                } catch (IOException e) {
                    getActivity().runOnUiThread(() -> {
                        showErrorSnackbar("Failed to fetch timetable: " + e.getMessage());
                    });
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
            showErrorSnackbar("An error occurred while fetching timetable: " + e.getMessage());
        }
    }

    private void saveTimeTableToFirebase(List<TimeTable> timeTableList) {
        try {
            FirebaseUser user = auth.getCurrentUser();
            if (user != null) {
                String userEmail = user.getEmail();
                if (userEmail != null) {
                    String sanitizedEmail = sanitizeEmail(userEmail);
                    DatabaseReference userRef = databaseReference.child(sanitizedEmail);

                    for (TimeTable timeTable : timeTableList) {
                        Map<String, Object> timetableData = new HashMap<>();
                        timetableData.put("departure", timeTable.getDeparture());
                        timetableData.put("arrival", timeTable.getArrival());
                        timetableData.put("duration", timeTable.getDuration());
                        timetableData.put("trainEndsAt", timeTable.getTrainEndsAt());
                        timetableData.put("trainNo", timeTable.getTrainNo());
                        timetableData.put("trainType", timeTable.getTrainType());

                        userRef.push().setValue(timetableData)
                                .addOnSuccessListener(aVoid -> Log.d(TAG, "Data saved successfully"))
                                .addOnFailureListener(e -> {
                                    Log.w(TAG, "Error saving data", e);
                                    showErrorSnackbar("Error saving data: " + e.getMessage());
                                });
                    }
                } else {
                    showErrorSnackbar("User email is null.");
                }
            } else {
                showErrorSnackbar("User is not authenticated.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showErrorSnackbar("An error occurred while saving timetable to Firebase: " + e.getMessage());
        }
    }

    private void fetchTimeTableFromFirebase() {
        try {
            FirebaseUser user = auth.getCurrentUser();
            if (user != null) {
                String userEmail = user.getEmail();
                if (userEmail != null) {
                    String sanitizedEmail = sanitizeEmail(userEmail);
                    DatabaseReference userRef = databaseReference.child(sanitizedEmail);

                    userRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            try {
                                List<TimeTable> timeTableList = new ArrayList<>();
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    TimeTable timeTable = snapshot.getValue(TimeTable.class);
                                    if (timeTable != null) {
                                        timeTableList.add(timeTable);
                                    }
                                }
                                timeTableViewModel.setTimeTableList(timeTableList);
                            } catch (Exception e) {
                                e.printStackTrace();
                                showErrorSnackbar("An error occurred while processing timetable data: " + e.getMessage());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            showErrorSnackbar("Failed to fetch timetable data: " + databaseError.getMessage());
                        }
                    });
                } else {
                    showErrorSnackbar("User email is null.");
                }
            } else {
                showErrorSnackbar("User is not authenticated.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showErrorSnackbar("An error occurred while fetching timetable from Firebase: " + e.getMessage());
        }
    }

    private String sanitizeEmail(String email) {
        return email.replace(".", "_").replace("@", "_");
    }

    private void resetFields() {
        try {
            binding.startStationSpinner.setSelection(0);
            binding.endStationSpinner.setSelection(0);
            binding.searchDateEditText.setText("");
            timeTableViewModel.setTimeTableList(null);
            deleteTimeTableFromFirebase();
            showSuccessSnackbar("Fields reset and timetable data deleted successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            showErrorSnackbar("An error occurred while resetting fields: " + e.getMessage());
        }
    }

    private void deleteTimeTableFromFirebase() {
        try {
            FirebaseUser user = auth.getCurrentUser();
            if (user != null) {
                String userEmail = user.getEmail();
                if (userEmail != null) {
                    String sanitizedEmail = sanitizeEmail(userEmail);
                    DatabaseReference userRef = databaseReference.child(sanitizedEmail);

                    userRef.removeValue()
                            .addOnSuccessListener(aVoid -> Log.d(TAG, "Timetable data deleted successfully"))
                            .addOnFailureListener(e -> {
                                Log.w(TAG, "Error deleting timetable data", e);
                                showErrorSnackbar("Error deleting timetable data: " + e.getMessage());
                            });
                } else {
                    showErrorSnackbar("User email is null.");
                }
            } else {
                showErrorSnackbar("User is not authenticated.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showErrorSnackbar("An error occurred while deleting timetable from Firebase: " + e.getMessage());
        }
    }

    private void populateSpinner(Spinner spinner, List<String> items) {
        try {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, items);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
            showErrorSnackbar("An error occurred while populating spinner: " + e.getMessage());
        }
    }

    private void showDatePicker() {
        try {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                    (view, year1, monthOfYear, dayOfMonth) -> {
                        String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1;
                        binding.searchDateEditText.setText(selectedDate);
                    }, year, month, day);
            datePickerDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
            showErrorSnackbar("An error occurred while showing date picker: " + e.getMessage());
        }
    }

    private void showErrorSnackbar(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
    }

    private void showSuccessSnackbar(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}