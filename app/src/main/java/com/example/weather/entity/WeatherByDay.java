package com.example.weather.entity;

import android.app.Activity;

import com.example.weather.MainActivity;
import com.example.weather.R;

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

    public String getMaxTString() {
        return getTemperatureString();
    }

    public String getMinTString() {
        if(getSharedPreferences().getString(MainActivity.UNIT_TEMPRATURE,null).equals(getCtx().getString(R.string.celcius))){
            return Math.round(minT)+" "+ getCtx().getString(R.string.celcius);
        }
        return Math.round(minT*9/5+32)+" "+getCtx().getString(R.string.farengeit);
    }
}
