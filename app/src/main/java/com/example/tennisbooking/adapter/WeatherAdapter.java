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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_item, parent, false);
        return new WeatherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherViewHolder holder, int position) {
        WeatherForecast weather = weatherList.get(position);
        holder.tvDate.setText(weather.getDate());
        holder.tvTemperatureC.setText(String.format("Temperature (C): %d", weather.getTemperatureC()));
        holder.tvTemperatureF.setText(String.format("Temperature (F): %d", weather.getTemperatureF()));
        holder.tvSummary.setText(String.format("Summary: %s", weather.getSummary()));
    }

    @Override
    public int getItemCount() {
        return weatherList.size();
    }

    public static class WeatherViewHolder extends RecyclerView.ViewHolder {

        TextView tvDate, tvTemperatureC, tvSummary, tvTemperatureF;

        public WeatherViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.dateTextView);
            tvTemperatureC = itemView.findViewById(R.id.tempCTextView);
            tvTemperatureF = itemView.findViewById(R.id.tempFTextView);
            tvSummary = itemView.findViewById(R.id.summaryTextView);
        }
    }
}