package com.example.androidlevel2_lesson1.data;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.util.Log;

import androidx.fragment.app.Fragment;

import com.example.androidlevel2_lesson1.R;
import com.example.androidlevel2_lesson1.model.WeatherRequest;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.stream.Collectors;

//Не использую
public class ProcessingData extends Thread{
    private InputDataContainer inputDataContainer;
    private int t=0;
    private Handler handler;
    private FragmentOfData fragmentOfData;
    private BufferedReader obtainedDatal;

    public ProcessingData(Handler handler, FragmentOfData fragmentOfData, BufferedReader obtainedData) {
        this.handler = handler;
        this.fragmentOfData = fragmentOfData;
        this.obtainedDatal = obtainedData;
        inputDataContainer = new InputDataContainer();
    }
    @Override
    public void run() {
        final String result = getLines(obtainedDatal);
        Gson gson = new Gson();
        final WeatherRequest weatherRequest = gson.fromJson(result, WeatherRequest.class);
        handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                        fragmentOfData.displayWeather(writeWeather(weatherRequest));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                });
                Log.i("TAG", "ProcessingData");
        }

    private String getLines(BufferedReader in) {
        return in.lines().collect(Collectors.joining("\n"));
    }


    public InputDataContainer writeWeather(WeatherRequest weatherRequest) throws ParseException {
        Date dateCurrent=new Date();
        inputDataContainer.date = dateText(dateCurrent,weatherRequest);
        inputDataContainer.time = timeText(dateCurrent);
        Fragment fragment = ((Fragment) fragmentOfData);
        inputDataContainer.temperature =  fragment.getString(R.string.temperature, Math.round(weatherRequest.getList()[t].getMain().getTemp()));
        inputDataContainer.levelThermometer = Math.round(weatherRequest.getList()[t].getMain().getTemp());
        inputDataContainer.pressure = fragment.getString(R.string.txtPressure, weatherRequest.getList()[t].getMain().getPressure());
        inputDataContainer.windSpeed = fragment.getString(R.string.txtWindSpeed, Math.round(weatherRequest.getList()[t].getWind().getSpeed()));
        setArrayList(weatherRequest, fragment);
        return inputDataContainer;
    }

    private String dateText(Date dateCurrent, WeatherRequest weatherRequest) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        for(int i=0;i<weatherRequest.getList().length;i++){
            @SuppressLint("SimpleDateFormat") String dateString0=new SimpleDateFormat("dd.MM.yyyy HH:mm").format(weatherRequest.getList()[i].getDt()*1000);
            @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat();
            format.applyPattern("dd.MM.yyyy");
            Date dateSearch= format.parse(dateString0);
            if(dateCurrent.after(dateSearch)){
                t=i;
                break;
            }
        }
        return dateFormat.format(weatherRequest.getList()[t].getDt()*1000);
    }

    private String timeText(Date dateCurrent){
        DateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return timeFormat.format(dateCurrent);
    }

    private void setArrayList(WeatherRequest weatherRequest, Fragment fragment){
        Long weatherRequestCurrent, weatherRequestNext;
        DateFormat dateFormat = new SimpleDateFormat("dd.MM", Locale.getDefault());
        inputDataContainer.arrayListWeek=new ArrayList<>();
        inputDataContainer.arrayListTemperature=new ArrayList<>();
        for(int i=t+1;i<weatherRequest.getList().length-1;i++){
            weatherRequestCurrent = weatherRequest.getList()[i].getDt() * 1000;
            weatherRequestNext = weatherRequest.getList()[i + 1].getDt() * 1000;
            if(!dateFormat.format(weatherRequestCurrent).equals(dateFormat.format(weatherRequestNext))&
                    !dateFormat.format(weatherRequestCurrent).equals(dateFormat.format(weatherRequest.getList()[t].getDt() * 1000))) {
                inputDataContainer.arrayListWeek.add(dateFormat.format(weatherRequestCurrent));
                inputDataContainer.arrayListTemperature.add(fragment.getString(R.string.temperature, Math.round(weatherRequest.getList()[i].getMain().getTemp())));
            }
        }
    }
}
