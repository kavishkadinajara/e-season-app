package com.example.e_season.ui.timetable;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.e_season.R;
import com.example.e_season.databinding.FragmentTimetableBinding;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TimeTableFragment extends Fragment {

    private FragmentTimetableBinding binding;
    private TimeTableAdapter adapter;
    private TimeTableViewModel timeTableViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        timeTableViewModel = new ViewModelProvider(this).get(TimeTableViewModel.class);

        binding = FragmentTimetableBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        try {
            // Initialize RecyclerView
            binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new TimeTableAdapter(new ArrayList<>()); // Pass an empty list initially
            binding.recyclerView.setAdapter(adapter);

            // Observe the timetable data
            timeTableViewModel.getTimeTables().observe(getViewLifecycleOwner(), timeTables -> {
                if (timeTables != null) {
                    adapter.setTimeTableList(timeTables);
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
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "An error occurred during initialization: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        return root;
    }

    private void fetchAndPopulateSpinners() {
        TimeTableScraper scraper = new TimeTableScraper();
        new Thread(() -> {
            try {
                List<String> startStations = scraper.getStartStations();
                List<String> endStations = scraper.getEndStations();

                getActivity().runOnUiThread(() -> {
                    populateSpinner(binding.startStationSpinner, startStations);
                    populateSpinner(binding.endStationSpinner, endStations);
                });
            } catch (IOException e) {
                getActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Failed to fetch data: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
            }
        }).start();
    }

    private void fetchTimeTable() {
        try {
            final String startStation = binding.startStationSpinner.getSelectedItem().toString();
            final String endStation = binding.endStationSpinner.getSelectedItem().toString();
            final String date = binding.searchDateEditText.getText().toString();

            // Format the date to YYYY-MM-DD
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Calendar calendar = Calendar.getInstance();
            try {
                calendar.setTime(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(date));
                final String formattedDate = sdf.format(calendar.getTime());

                TimeTableScraper scraper = new TimeTableScraper();
                new Thread(() -> {
                    try {
                        List<TimeTable> timeTableList = scraper.getTimeTable(startStation, endStation, formattedDate);
                        getActivity().runOnUiThread(() -> timeTableViewModel.setTimeTableList(timeTableList));
                    } catch (IOException e) {
                        getActivity().runOnUiThread(() ->
                                Toast.makeText(getContext(), "Failed to fetch timetable: " + e.getMessage(), Toast.LENGTH_LONG).show()
                        );
                    }
                }).start();
            } catch (Exception e) {
                Toast.makeText(getContext(), "Invalid date format: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "An error occurred while fetching timetable: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void resetFields() {
        try {
            binding.startStationSpinner.setSelection(0);
            binding.endStationSpinner.setSelection(0);
            binding.searchDateEditText.setText("");
            timeTableViewModel.setTimeTableList(null);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "An error occurred while resetting fields: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void populateSpinner(Spinner spinner, List<String> items) {
        try {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, items);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "An error occurred while populating spinner: " + e.getMessage(), Toast.LENGTH_LONG).show();
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
            Toast.makeText(getContext(), "An error occurred while showing date picker: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}