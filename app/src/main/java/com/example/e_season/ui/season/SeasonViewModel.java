package com.example.e_season.ui.season;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class SeasonViewModel extends ViewModel {

    private final MutableLiveData<List<Season>> seasons;

    public SeasonViewModel() {
        seasons = new MutableLiveData<>();
        loadSeasons();
    }

    public LiveData<List<Season>> getSeasons() {
        return seasons;
    }

    private void loadSeasons() {
        // Simulate fetching data from an API
        List<Season> seasonList = new ArrayList<>();
        seasonList.add(new Season("Station 1", "Station A", "01/01/2023", "31/12/2023", "First Class"));
        seasonList.add(new Season("Station 2", "Station B", "01/01/2023", "31/12/2023", "Second Class"));
        seasonList.add(new Season("Station 3", "Station C", "01/01/2023", "31/12/2023", "Third Class"));
        seasons.setValue(seasonList);
    }
}