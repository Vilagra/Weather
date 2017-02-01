package com.example.weather.entity;

import android.app.Activity;
import android.util.Log;

import com.example.weather.MainActivity;
import com.example.weather.R;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by Vilagra on 16.11.2016.
 */

//!!!не уверен, насколько тут соблюденно правило наследования "является"?
public class WeatherByDay extends CurrentDisplayedWeather {
    @SerializedName("temperatureMin")
    private double minT;
    @SerializedName("temperatureMax")
    private double maxT;

    public double getMaxT() {
        return maxT;
    }

    public WeatherByDay(long time, String summary, int idDrawable, double maxT, double minT, double wind, double humidty, double precipProb) {
        super( time, summary, idDrawable, maxT, wind, humidty, precipProb);
        this.minT = minT;
    }

    public WeatherByDay(JSONObject jsonObject) {
        super(jsonObject);
        try {
            minT=jsonObject.getDouble("minT");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getMaxTString(String unit) {
        return getTemperatureString(unit,maxT);
    }

    public String getMinTString(String unit) {
        return getTemperatureString(unit,minT);
    }

    @Override
    public JSONObject getJSONObject() {
        JSONObject jsonObject = super.getJSONObject();
        try{
            jsonObject.put("minT",minT);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
