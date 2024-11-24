package com.example.e_season.ui.timetable;

public class TimeTable {
    private String startStation;
    private String endStation;
    private String startTime;
    private String endTime;

    public TimeTable(String startStation, String endStation, String startTime, String endTime) {
        this.startStation = startStation;
        this.endStation = endStation;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getStartStation() {
        return startStation;
    }

    public String getEndStation() {
        return endStation;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }
}