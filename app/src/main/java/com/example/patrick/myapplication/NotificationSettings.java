package com.example.patrick.myapplication;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Calendar;
import java.util.Date;

public class NotificationSettings extends AppCompatActivity {
    private PendingIntent pendingIntent;
    private TextView timeTextView;
    private TimePickerDialog.OnTimeSetListener timePick;
    private Calendar now;

    //SharedPreference field names
    public static final String PREF_TOGGLE = "savedToggle";
    public static final String PREF_HOUR   = "defaultHour";
    public static final String PREF_MINUTE = "defaultMinute";

    //Default time
    private final int DEFAULT_HOUR   = 18;
    private final int DEFAULT_MINUTE = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setTitle("Settings");

        timeTextView = (TextView) findViewById(R.id.textView_daily_time_reminder);
        now = Calendar.getInstance();
        //=====shared preferences
        final SharedPreferences preferences   = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editor = preferences.edit();

        /* Retrieve a PendingIntent that will perform a broadcast */
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
        ToggleButton toggle = (ToggleButton) findViewById(R.id.toggleButton_daily_reminder);
        //Restore the state of the toggle
        boolean savedToggleOn = preferences.getBoolean(PREF_TOGGLE, false);
        toggle.setChecked(savedToggleOn);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //Get the time from shared preferences
                    int h = preferences.getInt(PREF_HOUR, DEFAULT_HOUR);
                    int m = preferences.getInt(PREF_MINUTE, DEFAULT_MINUTE);
                    //Enable alarm
                    startAlarm(h, m);
                    //Store the state of the toggle
                    editor.putBoolean(PREF_TOGGLE, true).commit();
                } else {
                    //Disable alarm
                    cancelAlarm();
                    editor.putBoolean(PREF_TOGGLE, false).commit();
                }
            }
        });

        //Restore the users reminder settings for time
        int h = preferences.getInt(PREF_HOUR, DEFAULT_HOUR);
        int m = preferences.getInt(PREF_MINUTE, DEFAULT_MINUTE);
        String time = "Reminder Time\n " + formatTime(h, m);
        timeTextView.setText(time);
        timePick = new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hour, int minute) {
                timeTextView.setText("Reminder Time\n" + formatTime(hour, minute));
                //Store the time in shared preferences
                editor.putInt(PREF_HOUR, hour).commit();
                editor.putInt(PREF_MINUTE, minute).commit();
                //Get toggle state from shared preferences
                boolean alarmSetOn = preferences.getBoolean(PREF_TOGGLE, false);
                //Start alarm if toggle is set on
                if(alarmSetOn)
                    startAlarm(hour, minute);
            }
        };
    }

    //Used to select a time
    public void selectDailyTime(View view) {
        new TimePickerDialog(NotificationSettings.this, timePick, now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE), true).show();
    }

    //Starts a repeating alarm for the specified time, from the time-picker
    private void startAlarm(int hour, int minute) {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        long interval = 60000; //minute
        //Un-comment the line above and comment out the line below to quickly see
        // that notifications are repeating.
        //long interval = AlarmManager.INTERVAL_DAY;
        long trigger = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour); //24 hour clock
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        //calendar.add(Calendar.DAY_OF_MONTH, -1);
        Calendar current  = Calendar.getInstance();
        //Apply this if statement so the alarm for a notification isn't fired instantly
        if(calendar.compareTo(current) <= 0)
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        trigger = calendar.getTimeInMillis();
        manager.setRepeating(AlarmManager.RTC_WAKEUP, trigger, interval, pendingIntent);
        //noinspection ResourceType
        Toast.makeText(this, "Daily Reminder Set for " + formatTime(hour, minute), Toast.LENGTH_LONG).show();
    }

    //Turns off the alarm and in turn notifications
    private void cancelAlarm() {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);
        Toast.makeText(this, "Daily Reminders are now off", Toast.LENGTH_LONG).show();
    }


    //Formats the time into the format HH:mm, eg. 02:07 am/pm
    private String formatTime(int hour, int minute) {
        //Format the time
        String format = "";
        if(hour < 10)
            format = "0";
        format += hour + ":";
        if(minute < 10)
            format += "0";
        format += minute;
        if(hour < 12)
            format += " am";
        else
            format += " pm";
        return format;
    }

    public void goToCalendar (MenuItem item) {
        Intent intent = new Intent(this, CalendarScreen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP); // To clean up all activities
        startActivity(intent);
        finish();
    }
    public void goToGraph(MenuItem item) {
        Intent intent = new Intent(this, GraphActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP); // To clean up all activities
        startActivity(intent);
        finish();
    }
    public void goToMain(MenuItem item) {
        DataBaseHelper dbh = new DataBaseHelper(this);
        if (dbh.getRating(Calendar.getInstance()) != -1) {
            dbh.close();
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP); // To clean up all activities
            startActivity(intent);
            finish();
        }
        else
            Toast.makeText(this, "No input for today", Toast.LENGTH_LONG).show();
    }
    public void goToSettings(MenuItem item) {}
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}