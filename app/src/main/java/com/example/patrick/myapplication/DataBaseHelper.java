package com.example.patrick.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Calendar;

class DataBaseHelper extends SQLiteOpenHelper {
    private static final int    DB_VERSION    = 1;
    private static final String DB_NAME       = "Ratings.db";
    // Table
    private static final String TABLE_RATINGS = "Ratings";
    // Columns
    private static final String YEAR          = "Year";        // Integer
    private static final String MONTH         = "Month";       // Integer
    private static final String DAY           = "Day";         // Integer
    private static final String RATING        = "Rating";      // Integer
    private static final String NOTES         = "Notes";       // Text
    private static final String ENTRY_ONE     = "Entry_one";   // Text
    private static final String ENTRY_TWO     = "Entry_two";   // Text
    private static final String ENTRY_THREE   = "Entry_three"; // Text
    private static final String IMAGE_PATH    = "Image_path";  // Text (the path to the image)

    public DataBaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    //Creates the database, the first time the app is run
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE =
                "CREATE TABLE "        +
                        TABLE_RATINGS  + " ("         +
                        YEAR           + " INTEGER, " +
                        MONTH          + " INTEGER, " +
                        DAY            + " INTEGER, " +
                        RATING         + " INTEGER, " +
                        NOTES          + " TEXT, "    +
                        ENTRY_ONE      + " TEXT, "    +
                        ENTRY_TWO      + " TEXT, "    +
                        ENTRY_THREE    + " TEXT, "    +
                        IMAGE_PATH     + " TEXT);";

        db.execSQL(CREATE_TABLE);
    }

    /*
      Inserts into the database if record not in database
      Otherwise the record is updated
      Returns whether or not the insert was successful
    */
    public boolean insert(int year, int month, int day, int rating, String notes, String entryOne,
                          String entryTwo, String entryThree, String imagePath){
        SQLiteDatabase db = this.getWritableDatabase(); //Get access to the database
        //Add entries to the appropriate columns
        ContentValues contentValues = new ContentValues();
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

        //Check is there already an entry
        if (getRow(year, month, day) == null)
            //If not insert the entry
            return db.insert(TABLE_RATINGS, null, contentValues) != -1;
            //There is an entry, so update it
        else {
            //Make sure to update the correct entry
            String condition = YEAR  + " == " + year  + " AND " +
                    MONTH + " == " + month + " AND " +
                    DAY   + " == " + day;
            return db.update(TABLE_RATINGS, contentValues, condition, null) > 0;
        }
    }

    //Inserts the entry on the current date into the database
    public boolean insertToday(int rating, String notes, String entryOne,
                               String entryTwo, String entryThree, String imagePath){
        //Get the current date
        Calendar c = Calendar.getInstance();
        int year   = c.get(Calendar.YEAR);
        int month  = c.get(Calendar.MONTH);
        int day    = c.get(Calendar.DAY_OF_MONTH);
        return insert(year, month, day, rating, notes, entryOne, entryTwo, entryThree, imagePath);
    }

    //Get all data associated to a particular daily entry
    public Rating getRow(int year, int month, int day) {
        String select = "SELECT " + RATING + "," + NOTES + "," + ENTRY_ONE + "," + ENTRY_TWO + "," +
                ENTRY_THREE  + "," + IMAGE_PATH + " FROM " + TABLE_RATINGS +
                " WHERE " + YEAR   + " == " + year  + " AND " +
                MONTH  + " == " + month + " AND " +
                DAY    + " == " + day;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor     = db.rawQuery(select, null); //Run select statement on database
        // If count is zero no row was returned
        if(cursor.getCount() == 0)
            return null;
        cursor.moveToFirst();
        int    rating     = cursor.getInt(0);
        String notes      = cursor.getString(1);
        String entryOne   = cursor.getString(2);
        String entryTwo   = cursor.getString(3);
        String entryThree = cursor.getString(4);
        String imagePath  = cursor.getString(5);
        cursor.close();
        return new Rating(year, month, day, rating, notes, entryOne, entryTwo, entryThree,
                imagePath);
    }

    //Get all data associated to a particular daily entry, based on a calendar object
    public Rating getRow(Calendar date) {
        return getRow(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH));
    }

    /*
        This method returns the rating for a particular day.
        It can also be used to check for an entry on a particular date
        returns -1 for a date without a rating
    */
    public int getRating(Calendar date) {
        String select = "SELECT " + RATING + " FROM " + TABLE_RATINGS +
                " WHERE "+ YEAR  + " == " + date.get(Calendar.YEAR)  + " AND " +
                MONTH + " == " + date.get(Calendar.MONTH) + " AND " +
                DAY   + " == " + date.get(Calendar.DAY_OF_MONTH);
        SQLiteDatabase db = this.getReadableDatabase(); //Get access to the database
        Cursor cursor     = db.rawQuery(select, null);  //Run the select statement on the database
        // If count is zero no row was returned
        if(cursor.getCount() == 0)
            return -1;
        cursor.moveToFirst();
        int rating = cursor.getInt(0);
        cursor.close(); //Close the cursor, prevents memory leak
        return rating;
    }

    public boolean delete(int year, int month, int day) {
        //Delete condition
        String condition = YEAR  + " == " + year  + " AND " +
                MONTH + " == " + month + " AND " +
                DAY   + " == " + day;
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_RATINGS, condition, null)>0;
    }

    public boolean delete(Calendar cal) {
        //Get the date
        int year   = cal.get(Calendar.YEAR);
        int month  = cal.get(Calendar.MONTH);
        int day    = cal.get(Calendar.DAY_OF_MONTH);
        return delete(year, month, day);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Implement some database migration code, if necessary
        //String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
        //db.execSQL(DROP_TABLE);
        //onCreate(db);
    }
}