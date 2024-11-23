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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.e_season.databinding.FragmentSeasonBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SeasonFragment extends Fragment {
    private static final String TAG = "SeasonFragment";
    private FragmentSeasonBinding binding;
    private SeasonAdapter seasonAdapter;
    private List<Season> seasonList;
    private List<String> townsList = new ArrayList<>();
    private static final String TOWNS_API_URL = "https://e-season-web.vercel.app/api/v2/town-list";

    private Spinner startStationSpinner, endStationSpinner;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SeasonViewModel seasonViewModel = new ViewModelProvider(this).get(SeasonViewModel.class);

        binding = FragmentSeasonBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize RecyclerView
        seasonList = new ArrayList<>();
        seasonAdapter = new SeasonAdapter(seasonList);
        binding.recyclerViewSeasons.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewSeasons.setAdapter(seasonAdapter);

        // Initialize Spinners
        startStationSpinner = binding.startStationSpinner;
        endStationSpinner = binding.endStationSpinner;

        // Fetch towns and populate spinners
        fetchTowns();

        // Observe seasons data
        seasonViewModel.getSeasons().observe(getViewLifecycleOwner(), seasons -> {
            seasonList.clear();
            seasonList.addAll(seasons);
            seasonAdapter.notifyDataSetChanged();
        });

        // Handle spinner selections
        binding.filterButton.setOnClickListener(v -> {
            String startStation = startStationSpinner.getSelectedItem().toString();
            String endStation = endStationSpinner.getSelectedItem().toString();

            if (!startStation.isEmpty() && !endStation.isEmpty()) {
                filterSeasons(startStation, endStation);
            } else {
                Toast.makeText(getContext(), "Please select both stations.", Toast.LENGTH_SHORT).show();
            }
        });

        // Get user ID from Firebase and print it to Logcat
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            Log.d(TAG, "User ID: " + userId);
        } else {
            Log.d(TAG, "No user is currently logged in.");
        }

        return root;
    }

    private void fetchTowns() {
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, TOWNS_API_URL, null,
                response -> {
                    try {
                        townsList.clear();
                        JSONObject jsonObject = new JSONObject(response.toString());
                        JSONArray townsArray = jsonObject.getJSONArray("towns");
                        if (townsArray != null && townsArray.length() > 0) {
                            for (int i = 0; i < townsArray.length(); i++) {
                                String townName = townsArray.optString(i, "Unknown");
                                townsList.add(townName);
                            }
    
                            Log.d(TAG, "Fetched towns: " + townsList);
    
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                                    android.R.layout.simple_spinner_item, townsList);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            startStationSpinner.setAdapter(adapter);
                            endStationSpinner.setAdapter(adapter);
                        } else {
                            Log.e(TAG, "API returned empty or null response.");
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Failed to fetch towns: " + e.getMessage());
                        Toast.makeText(getContext(), "Error parsing town list", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e(TAG, "Failed to fetch towns: " + error.getMessage());
                    Toast.makeText(getContext(), "Failed to fetch towns", Toast.LENGTH_SHORT).show();
                });
    
        queue.add(jsonArrayRequest);
    }
    
    

    private void filterSeasons(String startStation, String endStation) {
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
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}