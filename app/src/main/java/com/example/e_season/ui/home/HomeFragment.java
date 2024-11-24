package com.example.e_season.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.e_season.ApplySeasonActivity;
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

        // Observe ViewModel and set dynamic text
        homeViewModel.getText().observe(getViewLifecycleOwner(), binding.title::setText);

        // Setup click listeners for buttons
        binding.button1.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), ApplySeasonActivity.class);
            startActivity(intent);
        });

        // Example use of ImageView
        binding.homeImage.setOnClickListener(view ->
                Toast.makeText(getContext(), "Image Clicked", Toast.LENGTH_SHORT).show()
        );

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}