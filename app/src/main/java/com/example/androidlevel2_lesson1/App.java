package com.example.androidlevel2_lesson1;

import android.app.Application;

import com.example.androidlevel2_lesson1.model.db.WeatherDao;
import com.example.androidlevel2_lesson1.model.db.WeatherDatabase;
import com.example.androidlevel2_lesson1.model.db.WeatherSource;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class App extends Application {

    private static App instance;

    // База данных
    private WeatherDatabase db;

    private WeatherSource weatherSource;

    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        db = WeatherDatabase.createDB();
        weatherSource = new WeatherSource(getEducationDao());

    }

    public WeatherDao getEducationDao() {
        return db.getEducationDao();
    }

    public WeatherSource getWeatherSource() {
        return weatherSource;
    }

    public Gson gson() {
        return new GsonBuilder().setFieldNamingStrategy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .excludeFieldsWithoutExposeAnnotation().create();
    }
}

