package com.example.e_season.ui.timetable;

public class TimeTable {

    private String yourStation;
    private String arrivalTime;
    private String departureTime;
    private String destinationTime;
    private String endStationTime;
    private String frequency;
    private String name;
    private String type;

    public TimeTable(String yourStation, String arrivalTime, String departureTime, String destinationTime, String endStationTime, String frequency, String name, String type) {
        this.yourStation = yourStation;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
        this.destinationTime = destinationTime;
        this.endStationTime = endStationTime;
        this.frequency = frequency;
        this.name = name;
        this.type = type;
    }

    public String getYourStation() {
        return yourStation;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public String getDestinationTime() {
        return destinationTime;
    }

    public String getEndStationTime() {
        return endStationTime;
    }

    public String getFrequency() {
        return frequency;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}