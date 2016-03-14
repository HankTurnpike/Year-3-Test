package com.example.patrick.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "com.mycompany.myfirstapp.MESSAGE";
    private SeekBar ratingSlider;
    private int rating = 1;
    private TextView text;
    private DataBaseHelper   dbh;
    private EditText         editEntryOne, editEntryTwo, editEntryThree, editNotes;
    private ImageView        imageView;
    private static final int REQUEST_IMAGE = 1;
    private String           imagePath = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ratingSlider = (SeekBar) findViewById(R.id.seekBar);
        ratingSlider.setScaleY(1.25f);
        ratingSlider.setScaleX(1.25f);
        dbh            = new DataBaseHelper(this);
        editNotes      = (EditText) findViewById(R.id.notes);
        editEntryOne   = (EditText) findViewById(R.id.good);
        editEntryTwo   = (EditText) findViewById(R.id.good2);
        editEntryThree = (EditText) findViewById(R.id.good3);


        imageView      = (ImageView) findViewById(R.id.image_view_database);
        //Set fields for current day if in database
        text = (TextView) findViewById(R.id.slider_rating);
        ratingSlider.setMax(90);
        ratingSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = progress/10 +1;
                text.setText("Rating: " + progressChanged);

            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(MainActivity.this, "seek bar progress:" + progressChanged,
                        Toast.LENGTH_SHORT).show();
                rating = progressChanged;
                Toast.makeText(MainActivity.this, "rating:" + rating,
                        Toast.LENGTH_SHORT).show();
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setTitle("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);

    }
    public void goToDate (MenuItem item) {
        Intent intent = new Intent(this, DateScreen.class);
        Calendar calendar = Calendar.getInstance();
        int[] temp = {calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)};
        intent.putExtra("com.example.patrick.DATE",temp);
        startActivity(intent);
    }
    public void getImage(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            File image = createImageFile();
            imagePath = image.getAbsolutePath();
            // Continue only if the File was successfully created
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(image));
            startActivityForResult(intent, REQUEST_IMAGE);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE && resultCode == RESULT_OK) {
            if(imagePath != null) {
                Uri uri = Uri.parse(imagePath);
                imageView.setImageURI(uri);
                Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
                Bitmap thumbImg = ThumbnailUtils.extractThumbnail(bitmap, 100, 150);
                imageView.setImageBitmap(thumbImg);
            }
            //Gets the display name for the image, might be useful?
            //Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            //int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            //cursor.moveToFirst();
            //String testPath = cursor.getString(nameIndex);
        }
        else
            makeToast("No image captured.\nMemory may be full.");
    }

    public void insertRow(View view) {
        String text    = "Entry failed, your rating must be in the range 1 to 10!";
        String rateStr = ""+rating;
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
                Calendar temp = Calendar.getInstance();
                int month = temp.get(Calendar.MONTH);
                int day   = temp.get(Calendar.DAY_OF_MONTH);
                int year = temp.get(Calendar.YEAR);
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


    public void goToCalendar (MenuItem item) {
        Intent intent = new Intent(this, CalendarScreen.class);
        startActivity(intent);
    }
    public void goToDatabaseScreen(View view) {
        Intent intent = new Intent(this, InputScreen.class);
        startActivity(intent);
    }
    public void goToGraph(MenuItem item) {
        Intent intent = new Intent(this, GraphActivity.class);
        startActivity(intent);
    }
}
