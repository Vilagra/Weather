package com.example.weather;

import android.content.SharedPreferences;

import com.example.weather.entity.CurrentDisplayedWeather;
import com.example.weather.entity.Weather;
import com.example.weather.entity.WeatherByDay;
import com.example.weather.entity.WeatherByHours;

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

    public DataManager(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
        editor = sharedPreferences.edit();
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
        if(s!=null) {
            try {
                return new CurrentDisplayedWeather(new JSONObject(s));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void setCurrentDisplay(CurrentDisplayedWeather currentDisplay) {
        editor.putString(CURRENT_DISPLAY,currentDisplay.getJSONObject().toString());
        editor.commit();
    }

    public List<WeatherByDay> getWeatherByDays() {
        Set<String> setWeather= sharedPreferences.getStringSet(WEATHER_BY_DAYS,null);
        List<WeatherByDay> weatherByDays = new ArrayList<>();
        for (String s : setWeather) {
            try {
                weatherByDays.add(new WeatherByDay(new JSONObject(s)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Collections.sort(weatherByDays);
        return weatherByDays;
    }

    public void setWeatherByDays(List<Weather> currentWeatherList) {
        editor.putStringSet(WEATHER_BY_DAYS,getSet(currentWeatherList));
        editor.commit();
    }

    public List<WeatherByHours> getWeatherByHoursList() {
        Set<String> setWeather= sharedPreferences.getStringSet(WEATHER_BY_HOURS,null);
        List<WeatherByHours> weatherByHours = new ArrayList<>();
        for (String s : setWeather) {
            try {
                weatherByHours.add(new WeatherByHours(new JSONObject(s)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Collections.sort(weatherByHours);
        return weatherByHours;
    }

    public void setWeatherByHoursList(List<Weather> weatherByHoursList) {
        editor.putStringSet(WEATHER_BY_HOURS,getSet(weatherByHoursList));
        editor.commit();
    }

    public Set<String> getSet(List<Weather> weatherList){
        Set<String> setWeather=new HashSet<>();
        for (Weather weather : weatherList) {
            setWeather.add(weather.getJSONObject().toString());
        }
        return setWeather;
    }
}
