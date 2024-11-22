package com.example.e_season.ui.timetable;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.e_season.databinding.FragmentTimetableBinding;

import java.util.ArrayList;
import java.util.List;

public class TimeTableFragment extends Fragment {

    private FragmentTimetableBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        TimeTableViewModel timeTableViewModel =
                new ViewModelProvider(this).get(TimeTableViewModel.class);

        binding = FragmentTimetableBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

//        final TextView textView = binding.textTimetable;
//        timeTableViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        // Fetch data from API and populate spinners
        fetchAndPopulateSpinners();

        return root;
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

        List<String> startTimes = new ArrayList<>();
        startTimes.add("06:00 AM");
        startTimes.add("07:00 AM");
        startTimes.add("08:00 AM");

        List<String> endTimes = new ArrayList<>();
        endTimes.add("06:00 PM");
        endTimes.add("07:00 PM");
        endTimes.add("08:00 PM");

        // Populate spinners
        populateSpinner(binding.startStationSpinner, startStations);
        populateSpinner(binding.endStationSpinner, endStations);
        populateSpinner(binding.startTimeSpinner, startTimes);
        populateSpinner(binding.endTimeSpinner, endTimes);
    }

    private void populateSpinner(Spinner spinner, List<String> items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}