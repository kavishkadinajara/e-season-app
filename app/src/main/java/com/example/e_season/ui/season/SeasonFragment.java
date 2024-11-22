package com.example.e_season.ui.season;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.e_season.databinding.FragmentSeasonBinding;

public class SeasonFragment extends Fragment {
    private FragmentSeasonBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SeasonViewModel seasonViewModel = new ViewModelProvider(this).get(SeasonViewModel.class);

        binding = FragmentSeasonBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textSeason;
        seasonViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}