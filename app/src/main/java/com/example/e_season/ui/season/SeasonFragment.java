package com.example.e_season.ui.season;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.e_season.databinding.FragmentSeasonBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SeasonFragment extends Fragment {
    private static final String TAG = "SeasonFragment";
    private FragmentSeasonBinding binding;
    private SeasonAdapter seasonAdapter;
    private List<Season> seasonList;
    private List<String> stationsList = new ArrayList<>();

    private Spinner startStationSpinner, endStationSpinner;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSeasonBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        try {
            // Initialize Firebase Database and Auth
            databaseReference = FirebaseDatabase.getInstance().getReference("seasons");
            mAuth = FirebaseAuth.getInstance();

            // Initialize RecyclerView
            seasonList = new ArrayList<>();
            seasonAdapter = new SeasonAdapter(seasonList, getContext());
            binding.recyclerViewSeasons.setLayoutManager(new LinearLayoutManager(getContext()));
            binding.recyclerViewSeasons.setAdapter(seasonAdapter);

            // Initialize Spinners
            startStationSpinner = binding.startStationSpinner;
            endStationSpinner = binding.endStationSpinner;

            // Fetch stations and populate spinners
            fetchStations();

            // Create ViewModel with context
            SeasonViewModel seasonViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
                @NonNull
                @Override
                public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                    return (T) new SeasonViewModel(getContext());
                }
            }).get(SeasonViewModel.class);

            // Observe seasons data
            seasonViewModel.getSeasons().observe(getViewLifecycleOwner(), seasons -> {
                seasonList.clear();
                seasonList.addAll(seasons);
                seasonAdapter.notifyDataSetChanged();
            });

            // Handle spinner selections
            binding.filterButton.setOnClickListener(v -> {
                try {
                    String startStation = startStationSpinner.getSelectedItem().toString();
                    String endStation = endStationSpinner.getSelectedItem().toString();

                    if (!startStation.isEmpty() && !endStation.isEmpty()) {
                        filterSeasons(startStation, endStation);
                    } else {
                        Toast.makeText(getContext(), "Please select both stations.", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Exception in filterButton onClick", e);
                    Toast.makeText(getContext(), "An error occurred while filtering seasons", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Exception in onCreateView", e);
            Toast.makeText(getContext(), "An error occurred during initialization", Toast.LENGTH_SHORT).show();
        }

        return root;
    }

    private void fetchStations() {
        try {
            DatabaseReference stationsReference = FirebaseDatabase.getInstance().getReference("stations");
            stationsReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        stationsList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String station = snapshot.getValue(String.class);
                            if (station != null) {
                                stationsList.add(station);
                            }
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                                android.R.layout.simple_spinner_item, stationsList);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        startStationSpinner.setAdapter(adapter);
                        endStationSpinner.setAdapter(adapter);
                    } catch (Exception e) {
                        Log.e(TAG, "Exception in onDataChange", e);
                        Toast.makeText(getContext(), "An error occurred while fetching stations", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "Failed to fetch stations: " + databaseError.getMessage());
                    Toast.makeText(getContext(), "Failed to fetch stations", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Exception in fetchStations", e);
            Toast.makeText(getContext(), "An error occurred while fetching stations", Toast.LENGTH_SHORT).show();
        }
    }

    private void filterSeasons(String startStation, String endStation) {
        try {
            List<Season> filteredList = new ArrayList<>();
            for (Season season : seasonList) {
                if (season.getStartStation().equals(startStation) && season.getEndStation().equals(endStation)) {
                    filteredList.add(season);
                }
            }

            if (filteredList.isEmpty()) {
                Toast.makeText(getContext(), "No matching results found.", Toast.LENGTH_SHORT).show();
            } else {
                seasonAdapter.updateData(filteredList);
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception in filterSeasons", e);
            Toast.makeText(getContext(), "An error occurred while filtering seasons", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}