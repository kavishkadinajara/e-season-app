package com.example.e_season.ui.timetable;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TimeTableScraper {

    private static final String STATIONS_URL = "https://eservices.railway.gov.lk/schedule/searchTrain.action?lang=en";
    private static final String BASE_URL = "https://trainschedule.lk/schedule";

    public List<String> getStartStations() throws IOException {
        List<String> startStations = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(STATIONS_URL).get();
            Elements elements = doc.select("select#startStation option");
            for (Element element : elements) {
                String station = element.text();
                if (!station.isEmpty()) {
                    startStations.add(station);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
        return startStations;
    }

    public List<String> getEndStations() throws IOException {
        List<String> endStations = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(STATIONS_URL).get();
            Elements elements = doc.select("select#endStation option");
            for (Element element : elements) {
                String station = element.text();
                if (!station.isEmpty()) {
                    endStations.add(station);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
        return endStations;
    }

    public List<TimeTable> getTimeTable(String startStation, String endStation, String date) throws IOException {
        List<TimeTable> timeTableList = new ArrayList<>();
        try {
            String formattedStartStation = formatStationName(startStation);
            String formattedEndStation = formatStationName(endStation);
            String url = BASE_URL + "/achakaa/" + formattedStartStation + "-to-" + formattedEndStation + "-train-timetable?searchDate=" + date;
            Document doc = Jsoup.connect(url).get();

            Elements rows = doc.select("table#trainSchedule tbody tr");
            for (Element row : rows) {
                String startTime = row.select("td.startTime").text();
                String endTime = row.select("td.endTime").text();
                timeTableList.add(new TimeTable(startStation, endStation, startTime, endTime));
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
        return timeTableList;
    }

    private String formatStationName(String station) {
        if (station == null || station.isEmpty()) {
            return station;
        }
        return station.substring(0, 1).toUpperCase() + station.substring(1).toLowerCase();
    }
}