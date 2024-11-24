package com.example.e_season.ui.season;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.e_season.databinding.FragmentSeasonBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class SeasonFragment extends Fragment {
    private static final String TAG = "SeasonFragment";
    private FragmentSeasonBinding binding;
    private SeasonAdapter seasonAdapter;
    private List<Season> seasonList;

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

            // Create ViewModel with context and root view
            SeasonViewModel seasonViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
                @NonNull
                @Override
                public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                    return (T) new SeasonViewModel(getContext(), root);
                }
            }).get(SeasonViewModel.class);

            // Observe seasons data
            seasonViewModel.getSeasons().observe(getViewLifecycleOwner(), seasons -> {
                seasonList.clear();
                if (seasons != null && !seasons.isEmpty()) {
                    seasonList.addAll(seasons);
                    binding.noSeasonsTextView.setVisibility(View.GONE);
                } else {
                    binding.noSeasonsTextView.setVisibility(View.VISIBLE);
                }
                seasonAdapter.notifyDataSetChanged();
            });

            // Observe error messages
            seasonViewModel.getErrorMessage().observe(getViewLifecycleOwner(), this::showError);
        } catch (Exception e) {
            Log.e(TAG, "Exception in onCreateView", e);
            showError(e.getMessage());
        }

        return root;
    }

    private void showError(String errorMessage) {
        Toast.makeText(getContext(), "An error occurred: " + errorMessage, Toast.LENGTH_LONG).show();
        ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Error Message", errorMessage);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getContext(), "Error message copied to clipboard", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}