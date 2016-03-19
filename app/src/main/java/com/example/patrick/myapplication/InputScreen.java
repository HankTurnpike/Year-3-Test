package com.example.patrick.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class InputScreen extends AppCompatActivity {
    //Shared preferences names for initial start date of app
    public static String PREF_YEAR  = "START_YEAR";
    public static String PREF_MONTH = "START_MONTH";
    public static String PREF_DAY   = "START_DAY";

    private int rating = 1;
    private SeekBar ratingSlider;
    private TextView text;
    private DataBaseHelper dbh;
    private EditText editEntryOne, editEntryTwo, editEntryThree, editNotes;
    private ImageView imageView;
    private static final int REQUEST_IMAGE = 1;
    private String imagePath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        //Setup date for first run of application
        SharedPreferences preferences   = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        int runs = preferences.getInt("NumberOfLaunches", 1);
        if(runs < 2){
            //Set up start date in shared preferences, to limit when the calendar and graph
            // start to calculate when data is added to them
            //Comment out these three line and un-comment the four lines below.
            editor.putInt(PREF_YEAR, 2016).commit();
            editor.putInt(PREF_MONTH, 0).commit();
            editor.putInt(PREF_DAY, 1).commit();
            //Calendar now = Calendar.getInstance();
            //editor.putInt(PREF_YEAR, now.get(Calendar.YEAR)).commit();
            //editor.putInt(PREF_MONTH, now.get(Calendar.MONTH)).commit();
            //editor.putInt(PREF_DAY, now.get(Calendar.DAY_OF_MONTH)).commit();
            editor.putInt("NumberOfLaunches", ++runs).commit();
        }

        dbh = new DataBaseHelper(this);
        String check = getIntent().getStringExtra("redo");
        if(check==null && dbh.getRating(Calendar.getInstance())!=-1)
            goToMain();
        ratingSlider = (SeekBar) findViewById(R.id.seekBar);
        ratingSlider.setScaleY(1.25f);
        ratingSlider.setScaleX(1.25f);
        dbh            = new DataBaseHelper(this);
        editNotes      = (EditText) findViewById(R.id.notes);
        editEntryOne   = (EditText) findViewById(R.id.good);
        editEntryTwo   = (EditText) findViewById(R.id.good2);
        editEntryThree = (EditText) findViewById(R.id.good3);
        imageView      = (ImageView) findViewById(R.id.image_view_database);
        imageView.setVisibility(View.GONE);
        //Set fields for current day if in database
        text = (TextView) findViewById(R.id.slider_rating);
        ratingSlider.setMax(90);
        setContent();
        ratingSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged = rating;
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = (progress / 10) + 1;
                text.setText("Rating: " + progressChanged);
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                rating = progressChanged;
                Toast.makeText(InputScreen.this, "rating:" + rating,
                        Toast.LENGTH_SHORT).show();
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setTitle("Create Entry");
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
    public void getImage(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            File image = createImageFile();
            imagePath = image.getAbsolutePath();
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(image));
            startActivityForResult(intent, REQUEST_IMAGE);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Display image if File was successfully created
        if (requestCode == REQUEST_IMAGE && resultCode == RESULT_OK) {
            if(imagePath != null)
                displayImage();
        }
        else
            Toast.makeText(this, "No image captured.\nMemory may be full.", Toast.LENGTH_SHORT).show();
    }

    private void displayImage() {
        if(new File(imagePath).exists()) {
            imageView.setVisibility(View.VISIBLE);
            Uri uri = Uri.parse(imagePath);
            imageView.setImageURI(uri);
            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            Bitmap thumbImg = ThumbnailUtils.extractThumbnail(bitmap, 100, 150);
            imageView.setImageBitmap(thumbImg);
        }
    }

    public void insertRow(View view) {
        String text    = "Entry failed, your rating must be in the range 1 to 10!";
        String rateStr = ""+rating;
        int rating = Integer.parseInt(rateStr);
        if (rating > 0 && rating < 11) { //Test the range of the rating
            String entryOne   = editEntryOne.getText().toString().trim();
            String entryTwo   = editEntryTwo.getText().toString().trim();
            String entryThree = editEntryThree.getText().toString().trim();
            String notes = editNotes.getText().toString().trim();
            //Insert the data to database
            boolean success = dbh.insertToday(rating, notes, entryOne, entryTwo, entryThree,
                    imagePath);
            if (success) {
                text = "Entry successfully made ";
                dbh.close();
                goToMain();
            }
            else
                text = " Entry failed, memory may be full";
        }
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    private File createImageFile() {
        // Create an image file name
        String fileName = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(new Date());
        //Saves to external memory if present, otherwise internal memory is used.
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return new File(dir, "MindDiary_" + fileName + ".jpg");
    }
    //Move to main screen, removing ability to go back at the same time.
    private void goToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // To clean up all activities
        startActivity(intent);
        finish();
    }

    // Queries the database
    // Check for entry already made
    private void setContent() {
        Rating r = dbh.getRow(Calendar.getInstance());
        // If count is zero no row was returned
        if(r == null)
            return;
        //Get year, month, day
        rating = r.getRating();
        ratingSlider.setProgress(rating * 10);
        text.setText("Rating: " + rating);
        editNotes.setText(r.getNotes());
        editEntryOne.setText(r.getEntryOne());
        editEntryTwo.setText(r.getEntryTwo());
        editEntryThree.setText(r.getEntryThree());
        imagePath = r.getImagePath();
        if(new File(imagePath).exists())
            displayImage();
    }

    public void goToDebug(View view) {
        Intent intent = new Intent(this, DebugInput.class);
        startActivity(intent);
    }
    //Menu
    public void goToCalendar (MenuItem item) {
        Intent intent = new Intent(this, CalendarScreen.class);
        startActivity(intent);
    }
    public void goToGraph(MenuItem item) {
        Intent intent = new Intent(this, GraphActivity.class);
        startActivity(intent);
    }
    public void goToMain(MenuItem item) {
        if (dbh.getRating(Calendar.getInstance()) != -1){
            dbh.close();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        else
            Toast.makeText(this, "No input for today", Toast.LENGTH_LONG).show();
    }
    public void goToSettings(MenuItem item) {
        Intent intent = new Intent(this, NotificationSettings.class);
        startActivity(intent);
    }
}
