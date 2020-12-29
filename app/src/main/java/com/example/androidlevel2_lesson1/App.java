package com.example.androidlevel2_lesson1;

import android.app.Application;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.Log;

import com.example.androidlevel2_lesson1.dialog.MessageDialogFragment;
import com.example.androidlevel2_lesson1.dialog.OnFragmentDialogListener;
import com.example.androidlevel2_lesson1.model.db.WeatherDao;
import com.example.androidlevel2_lesson1.model.db.WeatherDatabase;
import com.example.androidlevel2_lesson1.model.db.WeatherSource;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class App extends Application {

    private static App instance;

    // База данных
    private WeatherDatabase db;
    private WeatherSource weatherSource;

    private NetworkReceiver networkReceiver;
    private BatteryReceiver batteryReceiver;

    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        db = WeatherDatabase.createDB();
        weatherSource = new WeatherSource(getEducationDao());

        networkReceiver = new NetworkReceiver();
        batteryReceiver = new BatteryReceiver();
        IntentFilter filterBattery = new IntentFilter();
        filterBattery.addAction("android.intent.action.BATTERY_LOW");
        registerReceiver(batteryReceiver, filterBattery);
        IntentFilter filterNetwork = new IntentFilter();
        filterNetwork.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(networkReceiver, filterNetwork);
        networkReceiver.onReceive(null,null);
    }

    public NetworkReceiver getNetworkReceiver() {
        return networkReceiver;
    }

    public BatteryReceiver getBatteryReceiver() {
        return batteryReceiver;
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

