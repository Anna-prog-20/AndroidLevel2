package com.example.weather.model.connection;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.BufferedReader;
import java.net.URL;

public class ServiceWeather extends Service {
    private ConnectionThread connectionThread;
    private final IBinder binder = new ServiceBinder();

    public ServiceWeather() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public synchronized int onStartCommand(Intent intent, int flags, int startId) {
        connectionThread = new ConnectionThread((URL)intent.getExtras().get("uri"));
        connectionThread.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean stopService(Intent name) {
        connectionThread.stopConnection();
        return super.stopService(name);
    }

    private BufferedReader getObtainedData() {
        return connectionThread.getObtainedData();
    }

    private ConnectionThread getConnectionThread() {
        return connectionThread;
    }


    public class ServiceBinder extends Binder {
        private final ServiceWeather serviceWeather = getService();
        public synchronized ServiceWeather getService() {
            Log.v("TAG","ServiceBinder getService ");
            return ServiceWeather.this;
        }
        public synchronized BufferedReader getObtainedData(){
            Log.v("TAG","ServiceBinder getObtainedData ");
            return serviceWeather.getObtainedData();
        }
        public synchronized boolean getConnection(){
            Log.v("TAG","ServiceBinder getConnection ");
            return serviceWeather.getConnectionThread().getConnect();
        }

    }

}