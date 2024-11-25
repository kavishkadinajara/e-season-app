package com.example.e_season.ui.timetable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_season.R;

import java.util.ArrayList;
import java.util.List;

public class TimeTableAdapter extends RecyclerView.Adapter<TimeTableAdapter.TimeTableViewHolder> {

    private List<TimeTable> timeTables;

    public TimeTableAdapter(List<TimeTable> timeTables) {
        this.timeTables = timeTables != null ? timeTables : new ArrayList<>();
    }

    @NonNull
    @Override
    public TimeTableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_timetable, parent, false);
        return new TimeTableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeTableViewHolder holder, int position) {
        TimeTable timeTable = timeTables.get(position);
        holder.departureTextView.setText("Departure: " + timeTable.getDeparture());
        holder.arrivalTextView.setText("Arrival: " + timeTable.getArrival());
        holder.durationTextView.setText("Duration: " + timeTable.getDuration());
        holder.trainEndsAtTextView.setText("Train Ends At: " + timeTable.getTrainEndsAt());
        holder.trainNoTextView.setText("Train No: " + timeTable.getTrainNo());
        holder.trainTypeTextView.setText("Train Type: " + timeTable.getTrainType());
    }

    @Override
    public int getItemCount() {
        return timeTables.size();
    }

    public void setTimeTableList(List<TimeTable> timeTables) {
        this.timeTables = timeTables != null ? timeTables : new ArrayList<>();
        notifyDataSetChanged();
    }

    static class TimeTableViewHolder extends RecyclerView.ViewHolder {

        TextView departureTextView;
        TextView arrivalTextView;
        TextView durationTextView;
        TextView trainEndsAtTextView;
        TextView trainNoTextView;
        TextView trainTypeTextView;

        public TimeTableViewHolder(@NonNull View itemView) {
            super(itemView);
            departureTextView = itemView.findViewById(R.id.departureTextView);
            arrivalTextView = itemView.findViewById(R.id.arrivalTextView);
            durationTextView = itemView.findViewById(R.id.durationTextView);
            trainEndsAtTextView = itemView.findViewById(R.id.trainEndsAtTextView);
            trainNoTextView = itemView.findViewById(R.id.trainNoTextView);
            trainTypeTextView = itemView.findViewById(R.id.trainTypeTextView);
        }
    }
}