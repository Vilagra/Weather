package com.example.weather;

import android.content.SharedPreferences;
import android.util.Log;

import com.example.weather.entity.CurrentDisplayedWeather;
import com.example.weather.entity.Weather;
import com.example.weather.entity.WeatherByDay;
import com.example.weather.entity.WeatherByHours;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Vilagra on 02.12.2016.
 */

public class DataManager {
    public static final String LOCATION = "location";
    public static final String NAME_OF_CITY = "name_of_city";
    public static final String CURRENT_DISPLAY = "current_display";
    public static final String WEATHER_BY_DAYS = "weather_by_days";
    public static final String WEATHER_BY_HOURS = "weather_by_hours";

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Gson gson;

    public DataManager(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
        editor = sharedPreferences.edit();
        gson=new Gson();
    }

    public String getLocation() {
        return sharedPreferences.getString(LOCATION,null);
    }

    public void setLocation(String location) {
        editor.putString(LOCATION,location);
        editor.commit();
    }

    public String getNameOfCity() {
        return sharedPreferences.getString(NAME_OF_CITY,null);
    }

    public void setNameOfCity(String nameOfCity) {
        editor.putString(NAME_OF_CITY,nameOfCity);
        editor.commit();
    }

    public CurrentDisplayedWeather getCurrentDisplay() {
        String s = sharedPreferences.getString(CURRENT_DISPLAY,null);
        return gson.fromJson(s,CurrentDisplayedWeather.class);
    }

    public void setCurrentDisplay(CurrentDisplayedWeather currentDisplay) {
        editor.putString(CURRENT_DISPLAY,new Gson().toJson(currentDisplay));
        editor.commit();
    }

    public List<WeatherByDay> getWeatherByDays() {
        String s = sharedPreferences.getString(WEATHER_BY_DAYS,null);
        return gson.fromJson(s,new TypeToken<List<WeatherByDay>>(){}.getType());
    }

    public void setWeatherByDays(List<Weather> currentWeatherList) {
        editor.putString(WEATHER_BY_DAYS, new Gson().toJson(currentWeatherList));
        editor.commit();
    }

    public List<WeatherByHours> getWeatherByHoursList() {
        String s = sharedPreferences.getString(WEATHER_BY_HOURS,null);
        return gson.fromJson(s,new TypeToken<List<WeatherByHours>>(){}.getType());
    }

    public void setWeatherByHoursList(List<Weather> weatherByHoursList) {
        editor.putString(WEATHER_BY_HOURS, new Gson().toJson(weatherByHoursList));
        editor.commit();
    }

}
