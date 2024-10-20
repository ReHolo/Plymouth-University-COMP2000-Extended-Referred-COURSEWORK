package com.example.tennisbooking.entity;

public class WeatherForecast {
    private String date;
    private int temperatureC;
    private int temperatureF;
    private String summary;

    // Getters and setters
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getTemperatureC() {
        return temperatureC;
    }

    public void setTemperatureC(int temperatureC) {
        this.temperatureC = temperatureC;
    }

    public int getTemperatureF() {
        return temperatureF;
    }

    public void setTemperatureF(int temperatureF) {
        this.temperatureF = temperatureF;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}