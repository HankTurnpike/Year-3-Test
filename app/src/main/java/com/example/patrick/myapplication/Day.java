package com.example.patrick.myapplication;

import java.util.Calendar;

public class Day {
    private int year, month, day;

    Day(int year0, int month0, int day0){
        year  = year0;
        month = month0;
        day   = day0;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public Calendar toCalendar() {
        Calendar c = Calendar.getInstance();
        c.set(year, month, day);
        return c;
    }

    @Override
    public String toString() {
        return day + "_" + month + "_" + year;
    }
}
