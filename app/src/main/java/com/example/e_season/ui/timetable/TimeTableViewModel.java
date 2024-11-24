package com.example.e_season.ui.timetable;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class TimeTableViewModel extends ViewModel {

    private final MutableLiveData<List<TimeTable>> timeTableList;

    public TimeTableViewModel() {
        timeTableList = new MutableLiveData<>();
    }

    public LiveData<List<TimeTable>> getTimeTableList() {
        return timeTableList;
    }

    public void setTimeTableList(List<TimeTable> timeTableList) {
        this.timeTableList.setValue(timeTableList);
    }
}