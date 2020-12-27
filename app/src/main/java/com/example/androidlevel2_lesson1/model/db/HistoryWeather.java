package com.example.androidlevel2_lesson1.model.db;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = {@Index(value = {"town","date","temp"})})
public class HistoryWeather {
    @PrimaryKey(autoGenerate = true)
    public long id;

    private String town;
    private String date;
    private String temp;

    public HistoryWeather() {

    }

    public HistoryWeather(String town, String date, String temp) {
        this.town = town;
        this.date = date;
        this.temp = temp;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }
}
