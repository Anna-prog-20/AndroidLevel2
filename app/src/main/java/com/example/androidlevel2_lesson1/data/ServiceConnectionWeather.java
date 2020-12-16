package com.example.androidlevel2_lesson1.data;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.BufferedReader;
import java.net.URL;

public class ServiceConnectionWeather extends Service {
    private Connection connection;
    private final IBinder binder = new ServiceBinder();

    public ServiceConnectionWeather() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            connection = new Connection((URL)intent.getExtras().get("uri"));
            connection.start();
            connection.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean stopService(Intent name) {
        connection.stopConnection();
        return super.stopService(name);
    }

    private BufferedReader getObtainedData() {
        return connection.getObtainedData();
    }

    private Connection getConnection() {
        return connection;
    }


    public class ServiceBinder extends Binder {
        ServiceConnectionWeather getService() {
            Log.i("TAG","ServiceBinder getService ");
            return ServiceConnectionWeather.this;
        }
        public BufferedReader getObtainedData(){
            Log.i("TAG","ServiceBinder getObtainedData ");
            return getService().getObtainedData();
        }
        public Connection getConnection(){
            Log.i("TAG","ServiceBinder getConnection ");
            return getService().getConnection();
        }

    }

}