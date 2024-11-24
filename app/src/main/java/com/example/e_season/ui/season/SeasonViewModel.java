package com.example.e_season.ui.season;

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
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    public SeasonViewModel() {
        seasons = new MutableLiveData<>();
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("seasons");
        loadSeasons();
    }

    public LiveData<List<Season>> getSeasons() {
        return seasons;
    }

    private void loadSeasons() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            databaseReference.orderByChild("userEmail").equalTo(userEmail).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<Season> seasonList = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Season season = snapshot.getValue(Season.class);
                        if (season != null) {
                            seasonList.add(season);
                        }
                    }
                    seasons.setValue(seasonList);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle possible errors.
                }
            });
        }
    }
}