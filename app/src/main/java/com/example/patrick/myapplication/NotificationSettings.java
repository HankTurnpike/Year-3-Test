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
    private ToggleButton toggle;
    private TextView timeTextView;
    private String time;
    private TimePickerDialog.OnTimeSetListener timePick;
    private Calendar now;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        timeTextView = (TextView) findViewById(R.id.textView_daily_time_reminder);
        now = Calendar.getInstance();
        SharedPreferences preferences   = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        String savedTime = preferences.getString("dailyRemTime", "no time selected");
        boolean savedToggleOn = preferences.getBoolean("savedToggle", false);


        /* Retrieve a PendingIntent that will perform a broadcast */
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
        toggle = (ToggleButton) findViewById(R.id.toggleButton_daily_reminder);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //Enable alarm
                    startAlarm();
                } else {
                    //Disable alarm
                }
            }
        });

        time = "Select time to be reminded: ";
        timeTextView.setText(time);
        timePick = new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hour, int minute) {
                //dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                //dateAndTime.set(Calendar.MINUTE, minute);
                //updateLabel();
                timeTextView.setText("Time - " + hour + ":" + minute);

            }
        };
    }

    public void selectTime(View view) {
        new TimePickerDialog(NotificationSettings.this, timePick, now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE), true).show();
    }
    public void startAlarm() {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        long interval = 60000; //minute
        //long interval = //AlarmManager.INTERVAL_DAY;
        long trigger = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 12); //24 hour clock
        calendar.set(Calendar.MINUTE, 30);
        calendar.set(Calendar.SECOND, 0);
        //calendar.add(Calendar.DAY_OF_MONTH, -1);
        Calendar current  = Calendar.getInstance();
        long before = calendar.getTimeInMillis();
        //Apply this if statement so the alarm for a notification isn't fired instantly
        //if(calendar.compareTo(current) <= 0)
        //    calendar.add(Calendar.DAY_OF_MONTH, 1);
        trigger = calendar.getTimeInMillis();
        manager.setRepeating(AlarmManager.RTC_WAKEUP, trigger, interval,
                pendingIntent);
        //noinspection ResourceType
        Toast.makeText(this, "Alarm Set\n" +
                "System time: " + System.currentTimeMillis() + "\nTrigger: " + trigger +
                "\nBefore: " + before, Toast.LENGTH_LONG).show();
    }
}