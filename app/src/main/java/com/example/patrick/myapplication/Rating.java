package com.example.patrick.myapplication;

public class Rating {
    private Day day;
    private int rating;
    private String  notes, entryOne, entryTwo, entryThree, imagePath;

    Rating(Day day0, int rating0, String notes0, String entryOne0, String entryTwo0,
           String entryThree0, String imagePath0){
        day        = day0;
        rating     = rating0;
        entryOne   = entryOne0;
        entryTwo   = entryTwo0;
        entryThree = entryThree0;
        notes      = notes0;
        imagePath  = imagePath0;
    }

    public String dateString() {
        return day.toString();
    }

    public Day getDay() {
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
