package com.example.weather.entity;

import android.app.Activity;
import android.content.Context;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Vilagra on 16.11.2016.
 */


public class WeatherByHours extends Weather{
    public WeatherByHours( long date, int idDrawable, double temperature, double wind) {
        super(date, idDrawable, temperature, wind);
    }

}
