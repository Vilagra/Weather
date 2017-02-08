package com.example.weather.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Bundle;

import com.example.weather.App;
import com.example.weather.DataManager;
import com.example.weather.api.WeatherApi;
import com.example.weather.entity.CurrentDisplayedWeather;
import com.example.weather.entity.Weather;
import com.example.weather.entity.WeatherByDay;
import com.example.weather.entity.WeatherByHours;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Created by Vilagra on 06.02.2017.
 */

public class WeatherLoader extends AsyncTaskLoader<String> {
    DataManager dataManager;
    String[] coordinates;

    public final static String COORDINATES = "coordinates";

    public WeatherLoader(Context context, Bundle bundle,DataManager dataManager) {
        super(context);
        this.dataManager=dataManager;
        coordinates=bundle.getStringArray(COORDINATES);

    }

    @Override
    public String loadInBackground() {
        InputStreamReader inputStream = null;
        try {
            WeatherApi weatherApi = App.getWeatherApi();
            Call<ResponseBody> call=weatherApi.getData(coordinates[1],coordinates[0],"si","ru");
            ResponseBody responseBody = call.execute().body();
            inputStream = new InputStreamReader(responseBody.byteStream());
            StringBuilder res = new StringBuilder("");
            char[] bytesBuffer = new char[1024];
            int read;
            while ((read = inputStream.read(bytesBuffer)) != -1) {
                res.append(bytesBuffer, 0, read);
            }
            JSONObject jsonObject = new JSONObject(res.toString());
            JSONObject currently = jsonObject.getJSONObject("currently");
            Gson gson = new Gson();
            CurrentDisplayedWeather currentDisplayedWeather = gson.fromJson(currently.toString(),CurrentDisplayedWeather.class);
            dataManager.setCurrentDisplay(currentDisplayedWeather);
            JSONObject dailly = jsonObject.getJSONObject("daily");
            JSONArray daillArray = dailly.getJSONArray("data");
            List<Weather> weatherByDayList = gson.fromJson(daillArray.toString(), new TypeToken<ArrayList<WeatherByDay>>(){}.getType());
            JSONObject hourly = jsonObject.getJSONObject("hourly");
            JSONArray hourlyArray = hourly.getJSONArray("data");
            List<Weather> weatherByHoursList = gson.fromJson(hourlyArray.toString(), new TypeToken<ArrayList<WeatherByHours>>(){}.getType());
            dataManager.setWeatherByDays(weatherByDayList);
            dataManager.setWeatherByHoursList(weatherByHoursList);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void deliverResult(String data) {
        super.deliverResult(data);
    }
}
