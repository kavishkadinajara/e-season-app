package com.example.e_season.ui.timetable;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TimeTableViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public TimeTableViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is the timetable fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}