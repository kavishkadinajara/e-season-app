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
        holder.yourStationTextView.setText(timeTable.getYourStation());
        holder.arrivalTimeTextView.setText(timeTable.getArrivalTime());
        holder.departureTimeTextView.setText(timeTable.getDepartureTime());
        holder.destinationTimeTextView.setText(timeTable.getDestinationTime());
        holder.endStationTimeTextView.setText(timeTable.getEndStationTime());
        holder.frequencyTextView.setText(timeTable.getFrequency());
        holder.nameTextView.setText(timeTable.getName());
        holder.typeTextView.setText(timeTable.getType());
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

        TextView yourStationTextView;
        TextView arrivalTimeTextView;
        TextView departureTimeTextView;
        TextView destinationTimeTextView;
        TextView endStationTimeTextView;
        TextView frequencyTextView;
        TextView nameTextView;
        TextView typeTextView;

        public TimeTableViewHolder(@NonNull View itemView) {
            super(itemView);
            yourStationTextView = itemView.findViewById(R.id.yourStationTextView);
            arrivalTimeTextView = itemView.findViewById(R.id.arrivalTimeTextView);
            departureTimeTextView = itemView.findViewById(R.id.departureTimeTextView);
            destinationTimeTextView = itemView.findViewById(R.id.destinationTimeTextView);
            endStationTimeTextView = itemView.findViewById(R.id.endStationTimeTextView);
            frequencyTextView = itemView.findViewById(R.id.frequencyTextView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            typeTextView = itemView.findViewById(R.id.typeTextView);
        }
    }
}