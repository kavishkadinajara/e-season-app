package com.example.e_season.ui.season;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_season.databinding.FragmentSeasonBinding;

import java.util.ArrayList;
import java.util.List;

public class SeasonFragment extends Fragment {
    private FragmentSeasonBinding binding;
    private SeasonAdapter seasonAdapter;
    private List<Season> seasonList;

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

        // Observe seasons data
        seasonViewModel.getSeasons().observe(getViewLifecycleOwner(), seasons -> {
            seasonList.clear();
            seasonList.addAll(seasons);
            seasonAdapter.notifyDataSetChanged();
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}