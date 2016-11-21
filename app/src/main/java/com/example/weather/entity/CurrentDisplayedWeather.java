package com.example.weather.entity;

import android.app.Activity;
import android.content.Context;

import com.example.weather.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Vilagra on 11.11.2016.
 */

public class CurrentDisplayedWeather extends Weather{

    private double humidity;
    private double precipProb;
    private String summary;

    public CurrentDisplayedWeather(Activity context, Date date, String summary, int idDrawable, double temperature, double wind, double humidty, double precipProb) {
        super(context,date,idDrawable,temperature,wind);
        this.humidity=humidty;
        this.precipProb=precipProb;
        this.summary=summary;
    }

    public String getSummary() {
        return summary;
    }

    public String getHumidityString() {
        return (int)(humidity*100)+"%";
    }

    public String getPrecipProbString() {
        return (int)(precipProb*100)+"%";
    }
}
