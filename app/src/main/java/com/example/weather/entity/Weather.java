package com.example.weather.entity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.weather.MainActivity;
import com.example.weather.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Vilagra on 16.11.2016.
 */

public class Weather {
    private Activity ctx;   //!!!храню контекст из-за того что мне нужен доступ к строковым ресурсам, наверно это не правильно? как правильно делать в данном случае? хранить контекст и
                             //иметь доступ к ресурсам или обходиться без ресурсов?
    private Date date;
    private int idDrawable;
    private double temperature;
    private double wind;
    private SharedPreferences sharedPreferences;

    public Weather(Activity ctx,Date date, int idDrawable, double temperature, double wind) {
        this.ctx=ctx;
        this.date = date;
        this.idDrawable = idDrawable;
        this.temperature = temperature;
        this.wind=wind;
        this.sharedPreferences=ctx.getSharedPreferences(ctx.getString(R.string.preference),Context.MODE_PRIVATE);
    }

    public Context getCtx() {
        return ctx;
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public String getStringDate() {
        if(sharedPreferences.getString(MainActivity.UNIT_DATE,null).equals(ctx.getString(R.string.dm))) {
            return new SimpleDateFormat("dd.MM").format(date.getTime());
        }
        return new SimpleDateFormat("MM.dd").format(date.getTime());
    }

    public String getStringHour() {
        if(sharedPreferences.getString(MainActivity.UNIT_TIME,null).equals(ctx.getString(R.string.h24))) {
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

    public String getTemperatureString() {
        if(sharedPreferences.getString(MainActivity.UNIT_TEMPRATURE,null).equals(ctx.getString(R.string.celcius))){
            return Math.round(temperature)+" "+ ctx.getString(R.string.celcius);
        }
        return Math.round(temperature*9/5+32)+" "+ctx.getString(R.string.farengeit);
    }

    public String getWindString() {
        if(sharedPreferences.getString(MainActivity.UNIT_WIND,null).equals(ctx.getString(R.string.mc))) {
            return wind + ": " + ctx.getString(R.string.windMeasure);
        }else if (sharedPreferences.getString(MainActivity.UNIT_WIND,null).equals(ctx.getString(R.string.km_h))){
            return String.format("%.2f",wind*3.6) + " " +ctx.getString(R.string.km_h);
        }
        return String.format("%.2f",wind*2.23694) + " " +ctx.getString(R.string.m_h);
    }
}
