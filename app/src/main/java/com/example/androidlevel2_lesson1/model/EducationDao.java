package com.example.androidlevel2_lesson1.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.androidlevel2_lesson1.model.HistoryWeather;

import java.util.Date;
import java.util.List;

@Dao
public interface EducationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertHistoryWeather(HistoryWeather historyWeather);

    @Update
    void updateHistoryWeather(HistoryWeather historyWeather);

    @Delete
    void deleteHistoryWeather(HistoryWeather historyWeather);

    @Query("DELETE FROM historyweather WHERE id = :id")
    void deleteHistoryWeatherById(long id);

    @Query("DELETE FROM historyweather")
    void deleteAllHistoryWeather();

    @Query("SELECT * FROM historyweather")
    List<HistoryWeather> getAllHistorWeather();

    @Query("SELECT * FROM historyweather WHERE id = :id")
    HistoryWeather getHistoryWeatherById(long id);

    @Query("SELECT * FROM historyweather WHERE town LIKE :town")
    List<HistoryWeather> getHistoryWeatherByTown(String town);

    @Query("SELECT * FROM historyweather WHERE date = :date")
    List<HistoryWeather> getHistoryWeatherByDate(String date);

    @Query("SELECT * FROM historyweather WHERE temp = :temp")
    List<HistoryWeather> getHistoryWeatherByTemp(String temp);

    @Query("SELECT * FROM historyweather ORDER BY id")
    List<HistoryWeather> getHistoryWeatherNotSort();

    @Query("SELECT * FROM historyweather ORDER BY town")
    List<HistoryWeather> getHistoryWeatherSortTown();

    @Query("SELECT * FROM historyweather ORDER BY date")
    List<HistoryWeather> getHistoryWeatherSortDate();

    @Query("SELECT * FROM historyweather ORDER BY temp")
    List<HistoryWeather> getHistoryWeatherSortTemp();

    @Query("SELECT COUNT() FROM historyweather")
    long getCountHistoryWeather();


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTown(Town town);

    @Update
    void updateTown(Town town);

    @Delete
    void deleteTown(Town town);

    @Query("DELETE FROM town WHERE id = :id")
    void deleteTownById(long id);

    @Query("DELETE FROM town")
    void deleteAllTown();

    @Query("SELECT * FROM town")
    List<Town> getAllTown();

    @Query("SELECT * FROM town WHERE id = :id")
    Town getTownById(long id);

    @Query("SELECT * FROM town WHERE town LIKE :town")
    List<Town> getTownByTown(String town);

    @Query("SELECT COUNT() FROM town")
    long getCountTown();

}
