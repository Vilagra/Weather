package com.example.weather.entity;

import android.app.Activity;
import android.content.Context;

import com.example.weather.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Vilagra on 11.11.2016.
 */

public class CurrentDisplayedWeather extends Weather{

    public static final String HUMIDITY = "humidity";
    public static final String PRECIP = "precip";
    public static final String SUMMARY = "summary";

    private double humidity;
    private double precipProb;
    private String summary;

    public CurrentDisplayedWeather(Activity context, Date date, String summary, int idDrawable, double temperature, double wind, double humidty, double precipProb) {
        super(context,date,idDrawable,temperature,wind);
        this.humidity=humidty;
        this.precipProb=precipProb;
        this.summary=summary;
    }

    public CurrentDisplayedWeather(JSONObject jsonObject) {
        super(jsonObject);
        try {
            humidity=jsonObject.getDouble(HUMIDITY);
            precipProb=jsonObject.getDouble(PRECIP);
            summary=jsonObject.getString(SUMMARY);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

    @Override
    public JSONObject getJSONObject() {
        JSONObject jsonObject=super.getJSONObject();
        try{
            jsonObject.put(HUMIDITY,humidity);
            jsonObject.put(PRECIP,precipProb);
            jsonObject.put(SUMMARY,summary);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
