package com.example.tennisbooking.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tennisbooking.Interface.BookingService;
import com.example.tennisbooking.R;
import com.example.tennisbooking.adapter.WeatherAdapter;
import com.example.tennisbooking.entity.WeatherForecast;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherFragment extends Fragment {
    private RecyclerView recyclerView;
    private WeatherAdapter weatherAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        fetchWeatherData();
        return view;
    }

    private void fetchWeatherData() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://web.socem.plymouth.ac.uk/COMP2000/ReferralApi/api/Bookings/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        BookingService weatherService = retrofit.create(BookingService.class);
        Call<List<WeatherForecast>> call = weatherService.getWeatherForecast();
        call.enqueue(new Callback<List<WeatherForecast>>() {
            @Override
            public void onResponse(Call<List<WeatherForecast>> call, @NonNull Response<List<WeatherForecast>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    weatherAdapter = new WeatherAdapter(response.body());
                    recyclerView.setAdapter(weatherAdapter);
                } else {
                    Toast.makeText(getActivity(), "Failed to retrieve data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<WeatherForecast>> call, @NonNull Throwable t) {
                Toast.makeText(getActivity(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}