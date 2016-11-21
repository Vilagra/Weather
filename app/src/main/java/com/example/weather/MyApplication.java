package com.example.weather;

import android.app.Application;

import com.example.weather.entity.CurrentDisplayedWeather;
import com.example.weather.entity.WeatherByDay;
import com.example.weather.entity.WeatherByHours;

import java.util.List;

/**
 * Created by Vilagra on 16.11.2016.
 */

public class MyApplication extends Application {
    private List<WeatherByDay> weatherByDays;
    private List<WeatherByHours> weatherByHoursList;
    private CurrentDisplayedWeather currentDisplay;
    String nameOfCity;
    String location;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getNameOfCity() {
        return nameOfCity;
    }

    public void setNameOfCity(String nameOfCity) {
        this.nameOfCity = nameOfCity;
    }

    public CurrentDisplayedWeather getCurrentDisplay() {
        return currentDisplay;
    }

    public void setCurrentDisplay(CurrentDisplayedWeather currentDisplay) {
        this.currentDisplay = currentDisplay;
    }

    public List<WeatherByDay> getWeatherByDays() {
        return weatherByDays;
    }

    public void setWeatherByDays(List<WeatherByDay> currentWeatherList) {
        this.weatherByDays = currentWeatherList;
    }

    public List<WeatherByHours> getWeatherByHoursList() {
        return weatherByHoursList;
    }

    public void setWeatherByHoursList(List<WeatherByHours> weatherByHoursList) {
        this.weatherByHoursList = weatherByHoursList;
    }
}
