package com.example.e_season.ui.timetable;

public class TimeTable {

    private String departure;
    private String arrival;
    private String duration;
    private String trainEndsAt;
    private String trainNo;
    private String trainType;

    // No-argument constructor required for Firebase
    public TimeTable() {
    }

    public TimeTable(String departure, String arrival, String duration, String trainEndsAt, String trainNo, String trainType) {
        this.departure = departure;
        this.arrival = arrival;
        this.duration = duration;
        this.trainEndsAt = trainEndsAt;
        this.trainNo = trainNo;
        this.trainType = trainType;
    }

    public String getDeparture() {
        return departure;
    }

    public String getArrival() {
        return arrival;
    }

    public String getDuration() {
        return duration;
    }

    public String getTrainEndsAt() {
        return trainEndsAt;
    }

    public String getTrainNo() {
        return trainNo;
    }

    public String getTrainType() {
        return trainType;
    }
}