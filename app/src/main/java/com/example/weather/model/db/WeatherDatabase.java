package com.example.weather.model.db;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.weather.App;

@Database(entities = {HistoryWeather.class, Town.class}, version = 1)
public abstract class WeatherDatabase extends RoomDatabase {
    private static final String DB_NAME = "weather";

    public abstract WeatherDao getEducationDao();

    public static WeatherDatabase createDB() {
        return Room.databaseBuilder(App.getInstance(), WeatherDatabase.class, DB_NAME)
                .build();
    }
}
