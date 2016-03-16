package com.example.patrick.myapplication;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private String imagePath ="";
    private ImageView imageView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        Calendar cal = Calendar.getInstance();
        //noinspection ConstantConditions
        getSupportActionBar().setTitle(cal.get(Calendar.YEAR) + "/" + cal.get(Calendar.MONTH) + "/" + cal.get(Calendar.DAY_OF_MONTH));

        DataBaseHelper dbh = new DataBaseHelper(this);
        TypedArray ar = this.getResources().obtainTypedArray(R.array.img_id_arr);
        int len = ar.length();
        int[] resIds = new int[len];
        for (int i = 0; i < len; i++)
            resIds[i] = ar.getResourceId(i, 0);
        ar.recycle();
        Rating data = dbh.getRow(cal);
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.layout);
        final RelativeLayout.LayoutParams params =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
        TextView notes = (TextView) findViewById(R.id.notes);
        if (!data.getNotes().equals("")) {
            notes.setText(data.getNotes());
        }
        else {
            notes.setText("No notes for this day.");
        }
            TextView goodThings = (TextView) findViewById(R.id.good_things);
        String goodText ="1. ";
        if(!data.getEntryOne().equals("")) {
            goodText = goodText+data.getEntryOne();
            if(!data.getEntryTwo().equals("")){
                goodText = goodText+"\n2. "+data.getEntryTwo();
                if(!data.getEntryThree().equals("")){
                    goodText = goodText+"\n3. "+data.getEntryThree();
                }
            }
        }
        else {
            goodThings.setVisibility(View.GONE);
            TextView goodTitle = (TextView) findViewById(R.id.title2);
            goodTitle.setVisibility(View.GONE);
        }
        goodThings.setText(goodText);
        imageView = (ImageView) findViewById(R.id.image);
        imagePath = data.getImagePath();
        displayImage();

        params.setMargins(0, 0, 0, 0);
        TextView rating = new TextView(this);
        rating.setLayoutParams(params);
        rating.setTextSize(66);
        rating.setText("" + data.getRating());
        rating.setWidth(300);
        rating.setHeight(300);
        rating.setTextColor(Color.parseColor("#FFFFFF"));
        rating.setGravity(17);

        Drawable d = ContextCompat.getDrawable(this, resIds[data.getRating() - 1]);
        rating.setBackground(d);
        layout.addView(rating);
    }

    private void displayImage(){
        if(!imagePath.equals("")) {
            Uri uri = Uri.parse(imagePath);
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageURI(uri);
        }
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    public void goToCalendar (View view) {
        Intent intent = new Intent(this, CalendarScreen.class);
        startActivity(intent);
    }
    public void goToGraph(View view) {
        Intent intent = new Intent(this, GraphActivity.class);
        startActivity(intent);
    }

    public void goToCalendar (MenuItem item) {
        Intent intent = new Intent(this, CalendarScreen.class);
        startActivity(intent);
    }
    public void goToGraph(MenuItem item) {
        Intent intent = new Intent(this, GraphActivity.class);
        startActivity(intent);
    }
    public void goToMain(MenuItem item) {}
    public void goToInput (View view) {
        Intent intent = new Intent(this, InputScreen.class);
        intent.putExtra("redo", "true");
        startActivity(intent);
    }
}
