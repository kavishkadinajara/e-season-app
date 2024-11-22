package com.example.e_season.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.e_season.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Observe ViewModel and set dynamic text
        homeViewModel.getText().observe(getViewLifecycleOwner(), binding.title::setText);

        // Setup click listeners for buttons
        binding.button1.setOnClickListener(view -> 
            Toast.makeText(getContext(), "Feature 1 Clicked", Toast.LENGTH_SHORT).show()
        );
        binding.button2.setOnClickListener(view -> 
            Toast.makeText(getContext(), "Feature 2 Clicked", Toast.LENGTH_SHORT).show()
        );
        binding.button3.setOnClickListener(view -> 
            Toast.makeText(getContext(), "Feature 3 Clicked", Toast.LENGTH_SHORT).show()
        );

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
