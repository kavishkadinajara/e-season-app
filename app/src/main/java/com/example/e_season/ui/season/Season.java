package com.example.e_season.ui.season;

public class Season {
    private String startStation;
    private String endStation;
    private String seasonStartDate;
    private String seasonEndDate;
    private String classType;
    private String userEmail;

    public Season(String startStation, String endStation, String seasonStartDate, String seasonEndDate, String classType, String userEmail) {
        this.startStation = startStation;
        this.endStation = endStation;
        this.seasonStartDate = seasonStartDate;
        this.seasonEndDate = seasonEndDate;
        this.classType = classType;
        this.userEmail = userEmail;
    }

    public String getStartStation() {
        return startStation;
    }

    public String getEndStation() {
        return endStation;
    }

    public String getSeasonStartDate() {
        return seasonStartDate;
    }

    public String getSeasonEndDate() {
        return seasonEndDate;
    }

    public String getClassType() {
        return classType;
    }

    public String getUserEmail() {
        return userEmail;
    }
}