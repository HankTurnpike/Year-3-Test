package com.example.patrick.myapplication;

public class Rating {
    private int year, month, day, rating;
    private String  notes, entryOne, entryTwo, entryThree, imagePath;

    Rating(int year0, int month0, int day0, int rating0, String notes0, String entryOne0, String
            entryTwo0,
           String entryThree0, String imagePath0){
        year       = year0;
        month      = month0;
        day        = day0;
        rating     = rating0;
        entryOne   = entryOne0;
        entryTwo   = entryTwo0;
        entryThree = entryThree0;
        notes      = notes0;
        imagePath  = imagePath0;
    }

    public String dateString() {
        return "" + day + "_" + month + "_" + year;
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

    public int getRating() {
        return rating;
    }

    public String getEntryOne() {
        return entryOne;
    }

    public String getEntryTwo() {
        return entryTwo;
    }

    public String getEntryThree() {
        return entryThree;
    }

    public String getNotes() {
        return notes;
    }

    public String getImagePath() {
        return imagePath;
    }
}
