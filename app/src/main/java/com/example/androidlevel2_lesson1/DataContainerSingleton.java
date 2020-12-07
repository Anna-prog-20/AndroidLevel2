package com.example.androidlevel2_lesson1;

import java.io.Serializable;

public class DataContainerSingleton implements Serializable {
    boolean windSpeed;
    boolean pressure;

    private static DataContainerSingleton instance;
    private DataContainerSingleton() {}

    static DataContainerSingleton getInstance() {
        if(instance == null) {
            instance = new DataContainerSingleton();
        }
        return instance;
    }
}
