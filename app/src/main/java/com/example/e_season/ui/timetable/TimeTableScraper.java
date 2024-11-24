package com.example.e_season.ui.timetable;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TimeTableScraper {

    private static final String URL = "https://eservices.railway.gov.lk/schedule/searchTrain.action?lang=en";

    public List<String> getStartStations() throws IOException {
        List<String> startStations = new ArrayList<>();
        Document doc = Jsoup.connect(URL).get();
        Elements options = doc.select("select#startStation option");
        for (Element option : options) {
            startStations.add(option.text());
        }
        return startStations;
    }

    public List<String> getEndStations() throws IOException {
        List<String> endStations = new ArrayList<>();
        Document doc = Jsoup.connect(URL).get();
        Elements options = doc.select("select#endStation option");
        for (Element option : options) {
            endStations.add(option.text());
        }
        return endStations;
    }

    public List<TimeTable> getTimeTable(String startStation, String endStation, String date) throws IOException {
        List<TimeTable> timeTables = new ArrayList<>();
        Document doc = Jsoup.connect(URL)
                .data("startStation", startStation)
                .data("endStation", endStation)
                .data("date", date)
                .post();
        Elements rows = doc.select("table tbody tr");

        for (Element row : rows) {
            Elements cols = row.select("td");
            if (cols.size() >= 8) {
                String yourStation = cols.get(0).text();
                String arrivalTime = cols.get(1).text();
                String departureTime = cols.get(2).text();
                String destinationTime = cols.get(3).text();
                String endStationTime = cols.get(4).text();
                String frequency = cols.get(5).text();
                String name = cols.get(6).text();
                String type = cols.get(7).text();

                TimeTable timeTable = new TimeTable(yourStation, arrivalTime, departureTime, destinationTime, endStationTime, frequency, name, type);
                timeTables.add(timeTable);
            }
        }
        return timeTables;
    }
}