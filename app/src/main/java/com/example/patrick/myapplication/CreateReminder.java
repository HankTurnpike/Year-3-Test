package com.example.patrick.myapplication;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;


public class CreateReminder extends AppCompatActivity {
    private TextView timeTextView;
    private String time;
    private TimePickerDialog.OnTimeSetListener timePick;
    private Calendar now;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_reminder);
        timeTextView = (TextView) findViewById(R.id.reminder_time);
        now = Calendar.getInstance();
        time = "Hello";
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
        new TimePickerDialog(CreateReminder.this, timePick, now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE), true).show();
    }
}
