package com.example.medconnectpro;

import java.io.Serializable;

public class DateModel implements Serializable {

    String date;
    int count;

    public DateModel() {
    }

    public DateModel(String date, int count) {
        this.date = date;
        this.count = count;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String name) {
        this.date = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}