package com.example.androidlevel2_lesson1.data;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.example.androidlevel2_lesson1.model.Main;
import com.example.androidlevel2_lesson1.town.MainActivity;

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
    public synchronized int onStartCommand(final Intent intent, int flags, int startId) {
        if (intent != null) {
            connectionThread = new ConnectionThread((URL) intent.getExtras().get("uri"));
            connectionThread.start();
            Log.i("TAG", "onStartCommand");
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean stopService(Intent name) {
        connectionThread.stopConnection();
        Log.i("TAG","ServiceWeather stopService");
        return super.stopService(name);
    }

    @Override
    public void onDestroy() {
        Log.i("TAG","onDestroy");
        super.onDestroy();

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
            Log.i("TAG","ServiceBinder getService ");
            return ServiceWeather.this;
        }
        public synchronized BufferedReader getObtainedData(){
            Log.i("TAG","ServiceBinder getObtainedData ");
            return serviceWeather.getObtainedData();
        }
        public synchronized boolean getConnection(){
            Log.i("TAG","ServiceBinder getConnection ");
            return serviceWeather.getConnectionThread().getConnect();
        }

    }

}