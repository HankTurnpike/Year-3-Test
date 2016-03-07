package com.example.patrick.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Calendar;

public class DataBaseHelper extends SQLiteOpenHelper {
    private static final int    DB_VERSION   = 1;
    private static final String DB_NAME      = "Ratings.db";
    private static final String TABLE_NAME   = "Ratings";
    // Columns
    private static final String COLUMN_ID    = "ID";          // Integer
    private static final String YEAR         = "Year";        // Integer
    private static final String MONTH        = "Month";       // Integer
    private static final String DAY          = "Day";         // Integer
    private static final String RATING       = "Rating";      // Integer
    private static final String NOTES        = "Notes";       // Text
    private static final String ENTRY_ONE    = "Entry_one";   // Text
    private static final String ENTRY_TWO    = "Entry_two";   // Text
    private static final String ENTRY_THREE  = "Entry_three"; // Text
    private static final String IMAGE_PATH   = "Image_path";  // Text (the path to the image)

    public DataBaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        //SQLiteDatabase db = this.getWritableDatabase(); // test the database is correctly created using
                                                          // Sqlite manager add-on in Firefox
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE =
                "CREATE TABLE "        +
                        TABLE_NAME     + " ("                                   +
                        YEAR           + " INTEGER, "                           +
                        MONTH          + " INTEGER, "                           +
                        DAY            + " INTEGER, "                           +
                        RATING         + " INTEGER, "                           +
                        NOTES          + " TEXT, "                              +
                        ENTRY_ONE      + " TEXT, "                              +
                        ENTRY_TWO      + " TEXT, "                              +
                        ENTRY_THREE    + " TEXT, "                              +
                        IMAGE_PATH     + " TEXT);";

        db.execSQL(CREATE_TABLE);
    }

    //Inserts into the database if record not in database
    //Otherwise the record is updated
    public boolean insert(int rating, String notes, String entryOne,
                          String entryTwo, String entryThree, String imagePath){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        Calendar c   = Calendar.getInstance();
        String year  = "" + c.get(Calendar.YEAR);
        String month = "" + c.get(Calendar.MONTH);
        String day   = "" + c.get(Calendar.DAY_OF_MONTH);
        contentValues.put(YEAR, year);
        contentValues.put(MONTH, month);
        contentValues.put(DAY, day);
        contentValues.put(RATING, rating);
        contentValues.put(NOTES, notes);
        contentValues.put(ENTRY_ONE, entryOne);
        contentValues.put(ENTRY_TWO, entryTwo);
        contentValues.put(ENTRY_THREE, entryThree);
        if(imagePath == null)
            imagePath = "";
        contentValues.put(IMAGE_PATH, imagePath);
        if (getRow(c) == null) // -1 return means a failed insert
            return db.insert(TABLE_NAME, null, contentValues) != -1;
        else { //Update failed if zero is returned
            String condition = YEAR  + " == " + year  + " AND " +
                               MONTH + " == " + month + " AND " +
                               DAY   + " == " + day;
            return db.update(TABLE_NAME, contentValues, condition, null) > 0;
        }
    }

    public Rating getRow(Calendar date) {
        String select = "SELECT * FROM " + TABLE_NAME +
                       " WHERE "+ YEAR   + " == " + date.get(Calendar.YEAR)  + " AND " +
                                  MONTH  + " == " + date.get(Calendar.MONTH) + " AND " +
                                  DAY    + " == " + date.get(Calendar.DAY_OF_MONTH);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(select, null);
        // If count is zero no row was returned
        if(cursor.getCount() == 0)
            return null;
        //Get year, month, day
        cursor.moveToFirst();
        Day    day        = new Day(cursor.getInt(0), cursor.getInt(1), cursor.getInt(2));
        int    rating     = cursor.getInt(3);
        String notes      = cursor.getString(4);
        String entryOne   = cursor.getString(5);
        String entryTwo   = cursor.getString(6);
        String entryThree = cursor.getString(7);
        String imagePath  = cursor.getString(8);
        cursor.close();
        return new Rating(day, rating, notes, entryOne, entryTwo, entryThree, imagePath);
        //return null;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Implement some database migration code, if necessary
        //String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
        //db.execSQL(DROP_TABLE);
        //onCreate(db);
    }
}