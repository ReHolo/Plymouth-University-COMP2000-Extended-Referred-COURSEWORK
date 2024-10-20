package com.example.tennisbooking.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tennisbooking.R;
import com.example.tennisbooking.entity.WeatherForecast;

import java.util.List;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder> {
    private List<WeatherForecast> weatherList;

    public WeatherAdapter(List<WeatherForecast> weatherList) {
        this.weatherList = weatherList;
    }

    @NonNull
    @Override
    public WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_weather, parent, false);
        return new WeatherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherViewHolder holder, int position) {
        WeatherForecast weather = weatherList.get(position);
        holder.dateTextView.setText(weather.getDate());
        holder.tempCTextView.setText("Temp (C): " + weather.getTemperatureC());
        holder.tempFTextView.setText("Temp (F): " + weather.getTemperatureF());
        holder.summaryTextView.setText("Summary: " + weather.getSummary());
    }

    @Override
    public int getItemCount() {
        return weatherList.size();
    }

    static class WeatherViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView, tempCTextView, tempFTextView, summaryTextView;

        public WeatherViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            tempCTextView = itemView.findViewById(R.id.tempCTextView);
            tempFTextView = itemView.findViewById(R.id.tempFTextView);
            summaryTextView = itemView.findViewById(R.id.summaryTextView);
        }
    }
}