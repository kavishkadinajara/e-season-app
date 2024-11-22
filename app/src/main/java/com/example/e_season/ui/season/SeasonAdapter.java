package com.example.e_season.ui.season;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_season.R;

import java.util.List;

public class SeasonAdapter extends RecyclerView.Adapter<SeasonAdapter.ViewHolder> {

    private List<Season> seasonList;

    public SeasonAdapter(List<Season> seasonList) {
        this.seasonList = seasonList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_season, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Season season = seasonList.get(position);
        holder.startStationTextView.setText(season.getStartStation());
        holder.endStationTextView.setText(season.getEndStation());
        holder.seasonStartDateTextView.setText(season.getSeasonStartDate());
        holder.seasonEndDateTextView.setText(season.getSeasonEndDate());
        holder.classTextView.setText(season.getClassType());
    }

    @Override
    public int getItemCount() {
        return seasonList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView startStationTextView;
        public TextView endStationTextView;
        public TextView seasonStartDateTextView;
        public TextView seasonEndDateTextView;
        public TextView classTextView;

        public ViewHolder(View view) {
            super(view);
            startStationTextView = view.findViewById(R.id.startStationTextView);
            endStationTextView = view.findViewById(R.id.endStationTextView);
            seasonStartDateTextView = view.findViewById(R.id.seasonStartDateTextView);
            seasonEndDateTextView = view.findViewById(R.id.seasonEndDateTextView);
            classTextView = view.findViewById(R.id.classTextView);
        }
    }
}