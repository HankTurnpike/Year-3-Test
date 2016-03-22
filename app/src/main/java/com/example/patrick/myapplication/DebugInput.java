package com.example.patrick.myapplication;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Input page used for putting date data in given locations.
 * For debug/demo only.
 */

public class DebugInput extends AppCompatActivity {
    private DataBaseHelper   dbh;
    private EditText         editEntryOne, editEntryTwo, editEntryThree, editRating, editNotes,
            editMonth, editDay;
    private ImageView        imageView;
    private static final int REQUEST_IMAGE = 1;
    private String           imagePath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug_input);

        dbh            = new DataBaseHelper(this);
        editRating     = (EditText) findViewById(R.id.rating);
        editNotes      = (EditText) findViewById(R.id.notes);
        editEntryOne   = (EditText) findViewById(R.id.entry_one);
        editEntryTwo   = (EditText) findViewById(R.id.entry_two);
        editEntryThree = (EditText) findViewById(R.id.entry_three);

        editMonth      = (EditText) findViewById(R.id.editText_month);
        editDay        = (EditText) findViewById(R.id.editText_day);

        imageView      = (ImageView) findViewById(R.id.image_view_database);
        //Set fields for current day if in database
        setContent();
    }

    public void getImage(View view){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            File image = createImageFile();
            imagePath = image.getAbsolutePath();
            // Continue only if the File was successfully created
            if (!imagePath.equals("")) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(image));
                startActivityForResult(intent, REQUEST_IMAGE);
            }
            else
                imagePath = "";
        }
        //Open a file explorer
    /*    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE_GET);*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE && resultCode == RESULT_OK) {
            displayImage();
            //Gets the display name for the image, might be useful?
            //Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            //int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            //cursor.moveToFirst();
            //String testPath = cursor.getString(nameIndex);
        }
        else
            makeToast("No image captured.\nMemory may be full.");
    }

    public void insert(View view) {
        //Get the current date
        Calendar c     = Calendar.getInstance();
        Calendar clone = (Calendar) c.clone();
        c.set(Calendar.YEAR, 2016);
        c.set(Calendar.MONTH, 0);
        c.set(Calendar.DAY_OF_MONTH, 1);
        while(!c.equals(clone)) {
            dbh.insert(
                    c.get(Calendar.YEAR),
                    c.get(Calendar.MONTH),
                    c.get(Calendar.DAY_OF_MONTH),
                    10,
                    "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor " +
                            "incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis " +
                            "nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. " +
                            "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu " +
                            "fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in " +
                            "culpa qui officia deserunt mollit anim id est laborum.",
                    "Sed ut perspiciatis unde omnis iste natus error sit voluptatem",
                    "At vero eos et accusamus et iusto odio dignissimos ducimus " +
                            "qui blanditiis praesentium ,",
                    "On the other hand, we denounce with righteous indignation and dislike",
                    imagePath);
            c.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    private int random() {
        return (int)Math.floor(Math.random() * 10) + 1;
    }

    public void insertRow(View view) {
        String text    = "Entry failed, your rating must be in the range 1 to 10!";
        String rateStr = editRating.getText().toString().trim();
        if(positiveInteger(rateStr)) { //check the rating is a positive integer
            int rating = Integer.parseInt(rateStr);
            if (rating > 0 && rating < 11) { //Test the range of the rating
                String entryOne   = editEntryOne.getText().toString().trim();
                String entryTwo   = editEntryTwo.getText().toString().trim();
                String entryThree = editEntryThree.getText().toString().trim();
                String notes      = editNotes.getText().toString().trim();
                //Insert the data to database
                //boolean success = dbh.insertToday(rating, notes, entryOne, entryTwo, entryThree,
                //        imagePath);
                int month = Integer.parseInt(editMonth.getText().toString());
                int day   = Integer.parseInt(editDay.getText().toString());
                boolean success = dbh.insert(2016, month, day, rating, notes, entryOne, entryTwo,
                        entryThree, imagePath);
                if (success)
                    text = "Entry successfully made ";
                else
                    text = " Entry failed, memory may be full";
            }
        }
        makeToast(text);
    }

    private File createImageFile() {
        // Create an image file name
        String fileName = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(new Date());
        //Saves to external memory if present, otherwise internal memory is used.
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return new File(dir, "MindDiary_" + fileName + ".jpg");
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