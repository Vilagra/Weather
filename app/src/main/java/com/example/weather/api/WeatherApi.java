package com.example.weather.api;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Vilagra on 01.02.2017.
 */

public interface WeatherApi {
    @GET("forecast/fff553af3244a00bd36d3c0b398dce88/{latitude},{longitude}")
    Call<ResponseBody> getData(@Path("latitude") String latitude, @Path("longitude") String longitude,
                               @Query("units") String units, @Query("lang") String language);
}
