package com.example.patrick.myapplication;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Calendar;

public class DataBaseActivity extends AppCompatActivity {
    private DataBaseHelper   dbh;
    private EditText         editEntryOne, editEntryTwo, editEntryThree, editRating, editNotes;
    private ImageView        imageView;
    private static final int REQUEST_IMAGE_GET = 1;
    private String           imagePath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.database_test_screen);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        dbh            = new DataBaseHelper(this);
        editRating     = (EditText) findViewById(R.id.rating);
        editNotes      = (EditText) findViewById(R.id.notes);
        editEntryOne   = (EditText) findViewById(R.id.entry_one);
        editEntryTwo   = (EditText) findViewById(R.id.entry_two);
        editEntryThree = (EditText) findViewById(R.id.entry_three);
        imageView      = (ImageView) findViewById(R.id.image_view_database);
        //Set fields for current day if in database
        setContent();
    }

    public void getImage(View view){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE_GET);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_GET && resultCode == RESULT_OK) {
            //Uri uri = data.getData();
            //imagePath = uri.getPath();
            //Get system path to image
            imagePath = data.getDataString();
            makeToast(imagePath);
            displayImage();

            //Gets the display name for the image, might be useful?
            //Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            //int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            //cursor.moveToFirst();
            //String testPath = cursor.getString(nameIndex);
        }
    }

    public void insertRow(View view) {
        String text    = "Entry failed, your rating must be in the range 1 to 10!";
        String rateStr = editRating.getText().toString().trim();
        if(positiveInteger(rateStr)) { //check the rating is a positive integer
            int rating = Integer.parseInt(rateStr);
            if (rating > 0 && rating < 11) { //Test the range of the rating
                String entryOne   = editEntryOne.getText().toString();
                String entryTwo   = editEntryTwo.getText().toString();
                String entryThree = editEntryThree.getText().toString();
                String notes      = editNotes.getText().toString();
                //Insert the data to database
                boolean success = dbh.insert(rating, notes, entryOne, entryTwo, entryThree, imagePath);
                if (success)
                    text = "Entry successfully made ";
                else
                    text = " Entry failed, memory may be full";
            }
        }
        makeToast(text);
    }

    // Queries the database
    // Check for entry already made
    private void setContent() {
        Rating rating = dbh.getRow(Calendar.getInstance());
        // If count is zero no row was returned
        if(rating == null)
            return;
        //Get year, month, day
        editRating.setText("" + rating.getRating());
        editNotes.setText(rating.getNotes());
        editEntryOne.setText(rating.getEntryOne());
        editEntryTwo.setText(rating.getEntryTwo());
        editEntryThree.setText(rating.getEntryThree());
        imagePath = rating.getImagePath();
        displayImage();
    }

    private void displayImage(){
        if(imagePath != null) {
            Uri uri = Uri.parse(imagePath);
            imageView.setImageURI(uri);
        }
    }

    private void makeToast(String text) {
        Context context  = getApplicationContext();
        int     duration = Toast.LENGTH_LONG;
        Toast   toast    = Toast.makeText(context, text, duration);
        toast.show();
    }

    private boolean positiveInteger(String num){
        num = num.trim();
        if(num.length() == 0)
            return false;
        String tmp = num;
        for(int i = 0; i < 10; i++)
            tmp = tmp.replace("" + i,"");
        return tmp.length() == 0;
    }
}

/*
text = "Rating:      " + rating     + "\n" +
        "Date:        " + date       + "\n" +
        "Entry one:   " + entryOne   + "\n" +
        "Entry two:   " + entryTwo   + "\n" +
        "Entry three: " + entryThree + "\n" +
        "Image Path:  " + imagePath;
 */