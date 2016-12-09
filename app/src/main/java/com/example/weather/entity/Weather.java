package com.example.weather.entity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.weather.MainActivity;
import com.example.weather.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by Vilagra on 16.11.2016.
 */

public class Weather implements Comparable<Weather> {

    private Date date;
    private int idDrawable;
    private double temperature;
    private double wind;

    public static final String DATE = "date";
    public static final String ID_DRAWABLE = "idDrawable";
    public static final String TEMPERATURE = "temperature";
    public static final String WIND = "wind";

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Weather weather = (Weather) o;

        return date.equals(weather.date);

    }

    @Override
    public int hashCode() {
        return date.hashCode();
    }

    @Override
    public int compareTo(Weather o) {
        if(this.equals(o)){
            return 0;
        }
        return this.date.getTime()>o.date.getTime()?1:-1;
    }

    public Weather(Date date, int idDrawable, double temperature, double wind) {
        this.date = date;
        this.idDrawable = idDrawable;
        this.temperature = temperature;
        this.wind=wind;
    }

    public Weather(JSONObject jsonObject) {
        try {
            date=new Date(jsonObject.getLong(DATE));
            idDrawable=jsonObject.getInt(ID_DRAWABLE);
            temperature=jsonObject.getDouble(TEMPERATURE);
            wind=jsonObject.getDouble(WIND);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject getJSONObject(){
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put(DATE,date.getTime());
            jsonObject.put(ID_DRAWABLE,idDrawable);
            jsonObject.put(TEMPERATURE,temperature);
            jsonObject.put(WIND,wind);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public String getStringDate(String unit) {
        if(unit.equals("ДД:ММ")) {
            return new SimpleDateFormat("dd.MM").format(date.getTime());
        }
        return new SimpleDateFormat("MM.dd").format(date.getTime());
    }

    public String getStringHour(String unit) {
        if(unit.equals("24 час")) {
            return new SimpleDateFormat("HH:mm").format(getDate().getTime());
        }
        return new SimpleDateFormat("hh:mm a").format(getDate().getTime());
    }

    public String getDayOfWeekShort(){
        return new SimpleDateFormat("EE").format(getDate().getTime());
    }

    public String getDayOfWeekLong(){
        String day=new SimpleDateFormat("EEEE").format(getDate().getTime());
        return day.substring(0, 1).toUpperCase() + day.substring(1);
    }

    public Date getDate() {
        return date;
    }

    public int getIdDrawable() {
        return idDrawable;
    }

    public String getTemperatureString(String unit) {
        if(unit.equals("°C")){
            return Math.round(temperature)+ unit;
        }
        return Math.round(temperature*9/5+32)+unit;
    }

    public String getWindString(String unit) {
        if(unit.equals("м/с")) {
            return wind + " " + unit;
        }else if (unit.equals("км/ч")){
            return String.format("%.2f",wind*3.6) + " " +unit;
        }
        return String.format("%.2f",wind*2.23694) + " " +unit;
    }




}
