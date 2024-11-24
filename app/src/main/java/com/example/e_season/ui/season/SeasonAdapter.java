package com.example.e_season.ui.season;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_season.R;

import java.util.List;

public class SeasonAdapter extends RecyclerView.Adapter<SeasonAdapter.ViewHolder> {

    private List<Season> seasonList;
    private Context context;

    public SeasonAdapter(List<Season> seasonList, Context context) {
        this.seasonList = seasonList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_season, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            Season season = seasonList.get(position);
            holder.seasonInfoTextView.setText("This season issued from " + season.getStartStation() + " to " + season.getEndStation());
            holder.seasonValidityTextView.setText("Season valid from " + season.getSeasonStartDate() + " to " + season.getSeasonEndDate());
        } catch (Exception e) {
            Toast.makeText(context, "An error occurred while binding data", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return seasonList.size();
    }

    public void updateData(List<Season> newSeasonList) {
        this.seasonList = newSeasonList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView seasonInfoTextView;
        public TextView seasonValidityTextView;

        public ViewHolder(View view) {
            super(view);
            seasonInfoTextView = view.findViewById(R.id.seasonInfoTextView);
            seasonValidityTextView = view.findViewById(R.id.seasonValidityTextView);
        }
    }
}