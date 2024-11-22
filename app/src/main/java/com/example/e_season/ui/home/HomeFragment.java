package com.example.e_season.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.e_season.R;
import com.example.e_season.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView titleTextView = binding.title;
        final ImageView homeImageView = binding.homeImage;
        final TextView descriptionTextView = binding.description;
        final Button button1 = binding.button1;
        final Button button2 = binding.button2;
        final Button button3 = binding.button3;

        homeViewModel.getText().observe(getViewLifecycleOwner(), titleTextView::setText);

        // Set up button click listeners
        button1.setOnClickListener(v -> {
            // Handle Feature 1 button click
        });

        button2.setOnClickListener(v -> {
            // Handle Feature 2 button click
        });

        button3.setOnClickListener(v -> {
            // Handle Feature 3 button click
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}