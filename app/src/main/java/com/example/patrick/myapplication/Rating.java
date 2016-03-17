package com.example.patrick.myapplication;
/*
Placeholder class for storing data associated to a daily entry
Useful for getting the information back from the database.

No error checks are carried out by this class, as the dependency
does not fall on it.
Dates (represented by year, month and day) are guaranteed to be valid, by the Calendar class
 */

public class Rating {
    //Represents the date the entry was made
    private final int year;
    private final int month;
    private final int day;
    //The rating entered by the user
    private final int rating;
    //Notes and entries made by the user
    private final String notes;
    private final String entryOne;
    private final String entryTwo;
    private final String entryThree;
    //Path to image take by user
    private final String imagePath;

    Rating(int year0, int month0, int day0, int rating0, String notes0, String entryOne0, String
            entryTwo0, String entryThree0, String imagePath0){
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


//======= Getter methods =========
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
