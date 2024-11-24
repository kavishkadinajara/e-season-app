package com.example.e_season.ui.timetable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_season.R;

import java.util.ArrayList;
import java.util.List;

public class TimeTableAdapter extends RecyclerView.Adapter<TimeTableAdapter.TimeTableViewHolder> {

    private List<TimeTable> timeTableList = new ArrayList<>();

    @NonNull
    @Override
    public TimeTableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_timetable, parent, false);
        return new TimeTableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeTableViewHolder holder, int position) {
        try {
            TimeTable timeTable = timeTableList.get(position);
            holder.startStationTextView.setText(timeTable.getStartStation());
            holder.endStationTextView.setText(timeTable.getEndStation());
            holder.startTimeTextView.setText(timeTable.getStartTime());
            holder.endTimeTextView.setText(timeTable.getEndTime());
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(holder.itemView.getContext(), "An error occurred while binding data: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public int getItemCount() {
        return timeTableList.size();
    }

    public void setTimeTableList(List<TimeTable> timeTableList) {
        this.timeTableList = timeTableList;
        notifyDataSetChanged();
    }

    static class TimeTableViewHolder extends RecyclerView.ViewHolder {

        TextView startStationTextView;
        TextView endStationTextView;
        TextView startTimeTextView;
        TextView endTimeTextView;

        public TimeTableViewHolder(@NonNull View itemView) {
            super(itemView);
            startStationTextView = itemView.findViewById(R.id.startStationTextView);
            endStationTextView = itemView.findViewById(R.id.endStationTextView);
            startTimeTextView = itemView.findViewById(R.id.startTimeTextView);
            endTimeTextView = itemView.findViewById(R.id.endTimeTextView);
        }
    }
}