package com.example.weather.entity;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Vilagra on 16.11.2016.
 */

public class Weather implements Comparable<Weather> {

    private long time;
    private int idDrawable;
    private String icon;
    private double temperature;
    @SerializedName("windSpeed")
    private double wind;

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public static final String DATE = "time";
    public static final String ID_DRAWABLE = "idDrawable";
    public static final String TEMPERATURE = "temperature";
    public static final String WIND = "wind";

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Weather weather = (Weather) o;

        return time ==weather.time;

    }

    @Override
    public int hashCode() {
        return (int) time;
    }

    @Override
    public int compareTo(Weather o) {
        if(this.equals(o)){
            return 0;
        }
        return this.time >o.time ?1:-1;
    }

    public Weather(Long time, int idDrawable, double temperature, double wind) {
        this.time = time;
        this.idDrawable = idDrawable;
        this.temperature = temperature;
        this.wind=wind;
    }

    public Weather(JSONObject jsonObject) {
        try {
            time =jsonObject.getLong(DATE);
            idDrawable=jsonObject.getInt(ID_DRAWABLE);
            temperature=jsonObject.getDouble(TEMPERATURE);
            wind=jsonObject.getDouble(WIND);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject getJSONObject(){
        Gson gson = new Gson();
        Log.d("my_log",gson.toJson(this));
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put(DATE, time);
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
            return new SimpleDateFormat("dd.MM").format(getTime().getTime());
        }
        return new SimpleDateFormat("MM.dd").format(getTime().getTime());
    }

    public String getStringHour(String unit) {
        if(unit.equals("24 час")) {
            return new SimpleDateFormat("HH:mm").format(getTime().getTime());
        }
        return new SimpleDateFormat("hh:mm a").format(getTime().getTime());
    }

    public String getDayOfWeekShort(){
        return new SimpleDateFormat("EE").format(getTime().getTime());
    }

    public String getDayOfWeekLong(){
        String day=new SimpleDateFormat("EEEE").format(getTime().getTime());
        return day.substring(0, 1).toUpperCase() + day.substring(1);
    }

    public Date getTime() {
        return new Date(time *1000);
    }

    public int getIdDrawable(Context context) {
        if(idDrawable==0){
            idDrawable=context.getResources().getIdentifier(icon.replaceAll("-", ""), "drawable", "com.example.weather");
        }
        return idDrawable;
    }

    public String getTemperatureString(String unit) {
        return getTemperatureString(unit,temperature);
    }
    public String getTemperatureString(String unit,double temperature) {
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
