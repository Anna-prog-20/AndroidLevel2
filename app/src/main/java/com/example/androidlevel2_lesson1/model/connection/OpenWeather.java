package com.example.androidlevel2_lesson1.model.connection;

import com.example.androidlevel2_lesson1.model.WeatherList;
import com.example.androidlevel2_lesson1.model.WeatherRequest;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenWeather {
    @GET("data/2.5/forecast")
    Call<WeatherRequest> loadWeather(@Query("q") String cityCountry, @Query("appid") String keyApi);

}
