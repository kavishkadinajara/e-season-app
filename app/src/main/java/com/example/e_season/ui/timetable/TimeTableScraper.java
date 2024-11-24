package com.example.e_season.ui.timetable;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TimeTableScraper {

    private static final String TAG = "TimeTableScraper";
    private static final String BASE_URL = "https://trainschedule.lk/schedule/aakshca/";
    private static final String STATIONS_URL = "https://eservices.railway.gov.lk/schedule/searchTrain.action?lang=en";

    public List<String> getStartStations() throws IOException {
        List<String> startStations = new ArrayList<>();
        Document doc = Jsoup.connect(STATIONS_URL).get();
        Elements options = doc.select("select#startStation option");
        for (Element option : options) {
            startStations.add(option.text());
        }
        Log.d(TAG, "Fetched Start Stations: " + startStations.size());
        return startStations;
    }

    public List<String> getEndStations() throws IOException {
        List<String> endStations = new ArrayList<>();
        Document doc = Jsoup.connect(STATIONS_URL).get();
        Elements options = doc.select("select#endStation option");
        for (Element option : options) {
            endStations.add(option.text());
        }
        Log.d(TAG, "Fetched End Stations: " + endStations.size());
        return endStations;
    }

    public List<TimeTable> getTimeTable(String startStation, String endStation) throws IOException {
        List<TimeTable> timeTables = new ArrayList<>();
        String url = BASE_URL + startStation.toLowerCase().replace(" ", "-") + "-to-" + endStation.toLowerCase().replace(" ", "-") + "-train-timetable";
        Document doc = Jsoup.connect(url).get();
        Elements rows = doc.select("table tbody tr");

        for (Element row : rows) {
            Elements cols = row.select("td");
            if (cols.size() >= 6) {
                String departure = cols.get(0).text();
                String arrival = cols.get(1).text();
                String duration = cols.get(2).text();
                String trainEndsAt = cols.get(3).text();
                String trainNo = cols.get(4).text();
                String trainType = cols.get(5).text();

                TimeTable timeTable = new TimeTable(departure, arrival, duration, trainEndsAt, trainNo, trainType);
                timeTables.add(timeTable);
            }
        }
        Log.d(TAG, "Fetched TimeTables: " + timeTables.size());
        return timeTables;
    }
}