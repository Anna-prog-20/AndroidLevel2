package com.example.androidlevel2_lesson1.model;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.androidlevel2_lesson1.App;

@Database(entities = {HistoryWeather.class, Town.class}, version = 1)
public abstract class EducationDatabase extends RoomDatabase {
    private static final String DB_NAME = "weather";

    public abstract EducationDao getEducationDao();

    public static EducationDatabase createDB() {
        return Room.databaseBuilder(App.getInstance(), EducationDatabase.class, DB_NAME)
                .build();
    }
}
