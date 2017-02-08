package com.example.weather;

import android.app.Application;
import android.util.Log;

import com.example.weather.api.CoordinatesApi;
import com.example.weather.api.WeatherApi;

import java.util.ArrayList;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Vilagra on 01.02.2017.
 */

public class App extends Application {

    private static WeatherApi weatherApi;
    private static CoordinatesApi coordinatesApi;
    private Retrofit retrofit;

    @Override
    public void onCreate() {
        super.onCreate();

        retrofit = new Retrofit.Builder()
                .baseUrl("https://api.darksky.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        weatherApi= retrofit.create(WeatherApi.class);
        retrofit=new Retrofit.Builder()
                .baseUrl("http://maps.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        coordinatesApi=retrofit.create(CoordinatesApi.class);

    }

    public static CoordinatesApi getCoordinatesApi() {
        return coordinatesApi;
    }

    public static WeatherApi getWeatherApi() {
        return weatherApi;
    }



}
