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
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Calendar;

public class NotificationSettings extends AppCompatActivity {
    private PendingIntent pendingIntent;
    private TextView timeTextView;
    private TimePickerDialog.OnTimeSetListener timePick;
    private Calendar now;

    //SharedPreference field names
    private final String PREF_TOGGLE = "savedToggle";
    private final String PREF_HOUR   = "defaultHour";
    private final String PREF_MINUTE = "defaultMinute";

    //Default time
    private final int DEFAULT_HOUR   = 17;
    private final int DEFAULT_MINUTE = 28;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        timeTextView = (TextView) findViewById(R.id.textView_daily_time_reminder);
        now = Calendar.getInstance();
        //=====shared preferneces
        final SharedPreferences preferences   = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editor = preferences.edit();

        /* Retrieve a PendingIntent that will perform a broadcast */
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
        ToggleButton toggle = (ToggleButton) findViewById(R.id.toggleButton_daily_reminder);
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

        int hour = preferences.getInt(PREF_HOUR, DEFAULT_HOUR);
        int minute = preferences.getInt(PREF_MINUTE, DEFAULT_MINUTE);
        String time = "Reminder Time\n " + hour + ":" + minute;
        timeTextView.setText(time);
        timePick = new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hour, int minute) {
                timeTextView.setText("Reminder Time\n" + hour + ":" + minute);
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



    public void selectDailyTime(View view) {
        new TimePickerDialog(NotificationSettings.this, timePick, now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE), true).show();
    }
    private void startAlarm(int hour, int minute) {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        long interval = 60000; //minute
        //long interval = //AlarmManager.INTERVAL_DAY;
        long trigger = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour); //24 hour clock
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        //calendar.add(Calendar.DAY_OF_MONTH, -1);
        Calendar current  = Calendar.getInstance();
        long before = calendar.getTimeInMillis();
        //Apply this if statement so the alarm for a notification isn't fired instantly
        if(calendar.compareTo(current) <= 0)
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        trigger = calendar.getTimeInMillis();
        manager.setRepeating(AlarmManager.RTC_WAKEUP, trigger, interval,
                pendingIntent);
        //noinspection ResourceType
        Toast.makeText(this, "Alarm Set\n" +
                "System time: " + System.currentTimeMillis() + "\nTrigger: " + trigger +
                "\nBefore: " + before, Toast.LENGTH_LONG).show();
    }

    private void cancelAlarm() {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);
    }
}