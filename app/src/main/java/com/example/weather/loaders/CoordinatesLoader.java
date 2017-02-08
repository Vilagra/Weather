package com.example.weather.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.example.weather.App;
import com.example.weather.DataManager;
import com.example.weather.api.CoordinatesApi;
import com.example.weather.api.WeatherApi;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Created by Vilagra on 07.02.2017.
 */

public class CoordinatesLoader extends AsyncTaskLoader<String[]>{
    String place;
    DataManager dataManager;

    public final static String CITY_NAME = "city_name";

    public CoordinatesLoader(Context context, Bundle bundle, DataManager dataManager) {
        super(context);
        this.dataManager=dataManager;
        place = bundle.getString(CITY_NAME);
    }

    @Override
    public String[] loadInBackground() {
        InputStreamReader in = null;
        Log.d("city",place);
        try {
            StringBuilder result = new StringBuilder();
            CoordinatesApi coordinatesApi = App.getCoordinatesApi();
            Call<ResponseBody> call=coordinatesApi.getData(place);
            ResponseBody responseBody = call.execute().body();
            in = new InputStreamReader(responseBody.byteStream());
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                result.append(buff, 0, read);
            }
            if (result == null) {
                return null;
            }
            JSONObject jsonObj = new JSONObject(result.toString());
            Log.d("jsonLOc",jsonObj.toString());
            JSONArray resultJsonArray = jsonObj.getJSONArray("results");
            JSONObject before_geometry_jsonObj = resultJsonArray.getJSONObject(0);
            JSONObject geometry_jsonObj = before_geometry_jsonObj.getJSONObject("geometry");
            JSONObject location_jsonObj = geometry_jsonObj.getJSONObject("location");
            String latitude = location_jsonObj.getString("lat");
            String longitude = location_jsonObj.getString("lng");
            dataManager.setLocation(before_geometry_jsonObj.getString("formatted_address"));
            dataManager.setNameOfCity(dataManager.getLocation().split(",")[0]);
            if (longitude != null && latitude != null) {
                String[] strings = new String[2];
                strings[0]=longitude;
                strings[1]=latitude;
                return strings;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
