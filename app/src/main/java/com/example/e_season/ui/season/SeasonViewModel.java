package com.example.e_season.ui.season;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SeasonViewModel extends ViewModel {

    private final MutableLiveData<List<Season>> seasons;
    private final MutableLiveData<String> errorMessage;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private Context context;
    private View rootView;

    public SeasonViewModel(Context context, View rootView) {
        this.context = context;
        this.rootView = rootView;
        seasons = new MutableLiveData<>();
        errorMessage = new MutableLiveData<>();
        mAuth = FirebaseAuth.getInstance();
        // Initialize Firebase Database with the default configuration
        databaseReference = FirebaseDatabase.getInstance().getReference("seasons");
        loadSeasons();
    }

    public LiveData<List<Season>> getSeasons() {
        return seasons;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    private void loadSeasons() {
        try {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                String userEmail = currentUser.getEmail();
                databaseReference.orderByChild("userEmail").equalTo(userEmail).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        try {
                            List<Season> seasonList = new ArrayList<>();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Season season = snapshot.getValue(Season.class);
                                if (season != null) {
                                    seasonList.add(season);
                                } else {
                                    Log.e("SeasonViewModel", "Season data is null for snapshot: " + snapshot);
                                }
                            }
                            seasons.setValue(seasonList);
                        } catch (Exception e) {
                            Log.e("SeasonViewModel", "Exception in onDataChange", e);
                            showError("An error occurred while loading seasons: " + e.getMessage());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("SeasonViewModel", "Failed to load seasons: " + databaseError.getMessage());
                        showError("Failed to load seasons: " + databaseError.getMessage());
                    }
                });
            } else {
                Log.e("SeasonViewModel", "Current user is null");
                showError("User not logged in");
            }
        } catch (Exception e) {
            Log.e("SeasonViewModel", "Exception in loadSeasons", e);
            showError("An error occurred while loading seasons: " + e.getMessage());
        }
    }

    private void showError(String errorMessage) {
        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Error Message", errorMessage);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(context, "Error message copied to clipboard", Toast.LENGTH_SHORT).show();
    }
}