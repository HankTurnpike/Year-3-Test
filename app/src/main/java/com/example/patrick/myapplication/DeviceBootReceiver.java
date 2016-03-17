package com.example.patrick.myapplication;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.util.Calendar;

//This class receives when the device has been booted/turned on
//It is used to prevent alarms/notifications from being disabled on device shutdown.
public class DeviceBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            SharedPreferences preferences   = PreferenceManager.getDefaultSharedPreferences(context);
            boolean alarmEnabled = preferences.getBoolean(NotificationSettings.PREF_TOGGLE, false);
            //Prevent notifications from being re-enabled, if they were set to disabled.
            if(!alarmEnabled)
                return;

            int hour   = preferences.getInt(NotificationSettings.PREF_HOUR, 18);
            int minute = preferences.getInt(NotificationSettings.PREF_MINUTE, 30);

            Intent alarmIntent = new Intent(context, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);

            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
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
            //Apply this if statement so the alarm for a notification isn't fired instantly
            //if(calendar.compareTo(current) <= 0)
            //    calendar.add(Calendar.DAY_OF_MONTH, 1);
            trigger = calendar.getTimeInMillis();
            manager.setRepeating(AlarmManager.RTC_WAKEUP, trigger, interval, pendingIntent);
        }
    }
}