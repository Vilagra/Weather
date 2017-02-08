package com.example.weather.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Vilagra on 07.02.2017.
 */

public interface CoordinatesApi {
    @GET("maps/api/geocode/json?sensor=false&language=ru")
    Call<ResponseBody> getData(@Query("address") String address);
}
