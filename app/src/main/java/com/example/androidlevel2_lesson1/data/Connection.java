package com.example.androidlevel2_lesson1.data;

import android.os.Handler;
import android.util.Log;

import androidx.fragment.app.FragmentManager;

import com.example.androidlevel2_lesson1.dialog.DialogBuilderFragment;
import com.example.androidlevel2_lesson1.model.WeatherRequest;
import com.example.androidlevel2_lesson1.weather.FragmentWeather;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.ParseException;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;

public class Connection extends Thread implements Runnable{
    private URL uri;
    private DialogBuilderFragment dlgBuilder;

    private HttpsURLConnection urlConnection = null;
    private boolean connect = false;
    private BufferedReader obtainedData = null;

    public Connection(URL uri) {
        this.uri = uri;
        initDialog();
    }

    private void initDialog() {
        dlgBuilder = new DialogBuilderFragment();
        dlgBuilder.setVisibleAddButton(false);
    }

    @Override
    public void run() {
        try {
            urlConnection = (HttpsURLConnection) uri.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000);

            if (urlConnection.getInputStream() != null) {
                obtainedData = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                connect = true;
            }
            else {
                connect = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopConnection() {
        if (null != urlConnection) {
            urlConnection.disconnect();
            try {
                obtainedData.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public boolean getConnect() {
        return connect;
    }

    public BufferedReader getObtainedData() {
        return obtainedData;
    }
}
