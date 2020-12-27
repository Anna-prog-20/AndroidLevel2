package com.example.androidlevel2_lesson1.model.dataTransfer;

import com.example.androidlevel2_lesson1.model.dataTransfer.InputDataContainer;

import java.text.ParseException;

public interface FragmentOfData {
    void displayWeather(InputDataContainer inputDataContainer) throws ParseException;
}
