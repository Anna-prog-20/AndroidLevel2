package com.example.weather.model.dataTransfer;

import android.annotation.SuppressLint;

import androidx.fragment.app.Fragment;

import com.example.weather.R;
import com.example.weather.model.WeatherRequest;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class PutData implements Runnable{
    private InputDataContainer inputDataContainer;
    private int t=0;

    private FragmentOfData fragmentOfData;
    private WeatherRequest weatherRequest;

    public PutData(FragmentOfData fragmentOfData, WeatherRequest weatherRequest) {
        this.fragmentOfData = fragmentOfData;
        this.weatherRequest = weatherRequest;
        inputDataContainer = new InputDataContainer();
    }

    @Override
    public void run() {
        try {
            fragmentOfData.displayWeather(writeWeather(weatherRequest));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    public InputDataContainer writeWeather(WeatherRequest weatherRequest) throws ParseException {
        Date dateCurrent=new Date();
        inputDataContainer.date = dateText(dateCurrent,weatherRequest);
        inputDataContainer.time = timeText(dateCurrent);
        Fragment fragment = ((Fragment) fragmentOfData);
        inputDataContainer.temperature =  fragment.getString(R.string.temperature, Math.round(weatherRequest.getList()[t].getMain().getTemp() - 273.15));
        inputDataContainer.levelThermometer = (int) Math.round(weatherRequest.getList()[t].getMain().getTemp() - 273.15);
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
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        inputDataContainer.arrayListWeek=new ArrayList<>();
        inputDataContainer.arrayListTemperature=new ArrayList<>();
        for(int i=t+1;i<weatherRequest.getList().length-1;i++){
            weatherRequestCurrent = weatherRequest.getList()[i].getDt() * 1000;
            weatherRequestNext = weatherRequest.getList()[i + 1].getDt() * 1000;
            if(!dateFormat.format(weatherRequestCurrent).equals(dateFormat.format(weatherRequestNext))&
                    !dateFormat.format(weatherRequestCurrent).equals(dateFormat.format(weatherRequest.getList()[t].getDt() * 1000))) {
                inputDataContainer.arrayListWeek.add(dateFormat.format(weatherRequestCurrent));
                inputDataContainer.arrayListTemperature.add(fragment.getString(R.string.temperature, Math.round(weatherRequest.getList()[i].getMain().getTemp()- 273.15)));
            }
        }
    }
}
