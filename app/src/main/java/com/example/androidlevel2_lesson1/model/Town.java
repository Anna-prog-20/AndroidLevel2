package com.example.androidlevel2_lesson1.model;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = {@Index(value = {"town"})})
public class Town {
    @PrimaryKey(autoGenerate = true)
    public long id;

    private String town;

    public Town() {

    }

    public Town(String town) {
        this.town = town;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

}
