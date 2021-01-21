package com.example.weather.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WeatherRequest {
    @SerializedName("list")
    @Expose
    private WeatherList[] list;

    public WeatherList[] getList() {
        return list;
    }

    public void setList(WeatherList[] list) {
        this.list = list;
    }
}
