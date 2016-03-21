package com.example.patrick.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;


import java.util.Calendar;
import java.util.Date;

@SuppressWarnings("ConstantConditions")
public class CalendarScreen extends AppCompatActivity implements OnDateSelectedListener {
    public final static String DATE = "com.example.patrick.DATE";
    private RelativeLayout layout;
    private MaterialCalendarView calendarView;
    private final OneDayDecorator oneDayDecorator = new OneDayDecorator();
    private DataBaseHelper dbh;
    //TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Calendar");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Set up the database and calendarView
        dbh = new DataBaseHelper(this);
        calendarView = (MaterialCalendarView) findViewById(R.id.calendarView);



        calendarView.setOnDateChangedListener(this);
        //Get array of rating drawable locations.
        TypedArray ar = this.getResources().obtainTypedArray(R.array.img_id_arr);
        int len = ar.length();
        int[] resIds = new int[len];
        for (int i = 0; i < len; i++)
            resIds[i] = ar.getResourceId(i, 0);
        ar.recycle();

        // Get start date of app from shared preferences
        SharedPreferences preferences   = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        int installYear  = preferences.getInt(InputScreen.PREF_YEAR, 2016);
        int installMonth = preferences.getInt(InputScreen.PREF_MONTH, 2);
        int installDay   = preferences.getInt(InputScreen.PREF_DAY, 18);

        Calendar cal = Calendar.getInstance();
        //Get the current day, days after should not have a rating
        //Clone is necessary, as times may be slightly different otherwise
        //   and therefore can never be equal, (for the while loop condition)
        Calendar tomorrow = (Calendar) cal.clone();
        tomorrow.add(Calendar.DAY_OF_MONTH, 1);

        //Set the initial install date for the app
        cal.set(Calendar.YEAR, installYear);
        cal.set(Calendar.MONTH, installMonth);
        cal.set(Calendar.DAY_OF_MONTH, installDay);

        CalendarDay day;
        //Search through all days since user started using the app
        //and decorate any day that has rating data.
        while (!cal.equals(tomorrow)) {
            int rating = dbh.getRating(cal);
            if(rating != -1) {
                day = CalendarDay.from(cal);
                calendarView.addDecorators(new OneDayDecorator(day,
                        ContextCompat.getDrawable(this,resIds[rating - 1])));
            }
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    //This method is called when the user taps a date on the calendar
    //If there is data for that day, the relevant DateScreen for that day is opened
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        CalendarDay current = CalendarDay.from(Calendar.getInstance());
        if((current.equals(date)||current.isAfter(date))&&dataExists(date)) {
            dbh.close();
            Intent intent = new Intent(this, DateScreen.class);
            int[] temp = {date.getYear(), date.getMonth(), date.getDay()};
            intent.putExtra(DATE, temp);
            startActivity(intent);
        }
    }
    //This checks if the day in question has any user data
    private boolean dataExists(CalendarDay date){
        Calendar temp = Calendar.getInstance();
        temp.set(date.getYear(),date.getMonth(),date.getDay());
        return(dbh.getRating(temp)!=-1);
    }

    //Menu
    public void goToCalendar (MenuItem item) {}
    public void goToGraph(MenuItem item) {
        dbh.close();
        Intent intent = new Intent(this, GraphActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP); // To clean up all activities
        startActivity(intent);
        finish();
    }
    public void goToMain(MenuItem item) {
        if (dbh.getRating(Calendar.getInstance()) != -1) {
            dbh.close();
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP); // To clean up all activities
            startActivity(intent);
            finish();
        }
        else
            Toast.makeText(CalendarScreen.this, "No input for today", Toast.LENGTH_LONG).show();
    }
    public void goToSettings(MenuItem item) {
        dbh.close();
        Intent intent = new Intent(this, NotificationSettings.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP); // To clean up all activities
        startActivity(intent);
        finish();
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}