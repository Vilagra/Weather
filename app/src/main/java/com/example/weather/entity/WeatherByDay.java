package com.example.weather.entity;

import android.app.Activity;

import com.example.weather.MainActivity;
import com.example.weather.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by Vilagra on 16.11.2016.
 */

//!!!не уверен, насколько тут соблюденно правило наследования "является"?
public class WeatherByDay extends CurrentDisplayedWeather {
    private double minT;

    public WeatherByDay(Activity context, Date date, String summary, int idDrawable, double maxT, double minT, double wind, double humidty, double precipProb) {
        super(context, date, summary, idDrawable, maxT, wind, humidty, precipProb);
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
        return getTemperatureString(unit);
    }

    public String getMinTString(String unit) {
        if(unit.equals("°C")){
            return Math.round(minT)+" "+ unit;
        }
        return Math.round(minT*9/5+32)+" "+unit;
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
