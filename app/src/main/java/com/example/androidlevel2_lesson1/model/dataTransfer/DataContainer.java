package com.example.androidlevel2_lesson1.model.dataTransfer;

import java.io.Serializable;
import java.util.ArrayList;

public class DataContainer implements Serializable {
    private int temperature;
    private int pressure;
    private int windSpeed;
    private boolean checkPressure=true;
    private boolean checkWindSpeed=true;
    private ArrayList<String> listTemperature;
    private String town;

    public boolean isCheckPressure() {
        return checkPressure;
    }

    public void setCheckPressure(boolean checkPressure) {
        this.checkPressure = checkPressure;
    }

    public boolean isCheckWindSpeed() {
        return checkWindSpeed;
    }

    public void setCheckWindSpeed(boolean checkWindSpeed) {
        this.checkWindSpeed = checkWindSpeed;
    }

    public DataContainer(String town) {
        this.town = town;
    }

    public String getTown() {
       return town;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public int getPressure() {
        return pressure;
    }

    public void setPressure(int pressure) {
        this.pressure = pressure;
    }

    public int getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(int windSpeed) {
        this.windSpeed = windSpeed;
    }

    public ArrayList<String> getListTemperature() {
        return listTemperature;
    }

    public void setListTemperature(ArrayList<String> listTemperature) {
        this.listTemperature = listTemperature;
    }

}
