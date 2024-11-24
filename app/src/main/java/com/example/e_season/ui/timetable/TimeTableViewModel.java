package com.example.e_season.ui.timetable;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class TimeTableViewModel extends ViewModel {

    private final MutableLiveData<List<TimeTable>> timeTables;

    public TimeTableViewModel() {
        timeTables = new MutableLiveData<>();
    }

    public LiveData<List<TimeTable>> getTimeTables() {
        return timeTables;
    }

    public void setTimeTableList(List<TimeTable> timeTableList) {
        timeTables.setValue(timeTableList);
    }
}