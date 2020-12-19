package com.example.androidlevel2_lesson1;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.room.Room;

import com.example.androidlevel2_lesson1.model.EducationDao;
import com.example.androidlevel2_lesson1.model.EducationDatabase;
import com.example.androidlevel2_lesson1.model.EducationSource;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class App extends Application {

    private static App instance;

    // База данных
    private EducationDatabase db;

    private EducationSource educationSource;

    // Получаем объект приложения
    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Сохраняем объект приложения (для Singleton’а)
        instance = this;

        // Строим базу
        db = EducationDatabase.createDB();
        educationSource = new EducationSource(getEducationDao());

    }

    // Получаем EducationDao для составления запросов
    public EducationDao getEducationDao() {
        return db.getEducationDao();
    }

    public EducationSource getEducationSource() {
        return educationSource;
    }

    public Gson gson() {
        return new GsonBuilder().setFieldNamingStrategy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .excludeFieldsWithoutExposeAnnotation().create();
    }
}

