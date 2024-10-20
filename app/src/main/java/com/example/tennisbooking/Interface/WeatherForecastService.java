package com.example.tennisbooking.Interface;

import com.example.tennisbooking.entity.WeatherForecast;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface WeatherForecastService {
    @GET("#/components/schemas/WeatherForecast")
    Call<List<WeatherForecast>> getWeatherForecast();
}
