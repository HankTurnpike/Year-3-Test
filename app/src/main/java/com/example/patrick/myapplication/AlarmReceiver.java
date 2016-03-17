package com.example.patrick.myapplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import java.util.Calendar;

//Class receives scheduled alarms made by the application in order
// to give the user a daily notification.
public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        DataBaseHelper dbh = new DataBaseHelper(context);
        //fire the notification only if no entry was made
        if(dbh.getRating(Calendar.getInstance()) == -1)  {
            Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            NotificationManager mNM = (NotificationManager) context.getSystemService(Context
                    .NOTIFICATION_SERVICE);
            Intent newIntent = new Intent(context.getApplicationContext(), InputScreen.class);
            PendingIntent pIntent = PendingIntent.getActivity(context, 0, newIntent, 0);

            Notification notification = new Notification.Builder(context)
                    .setContentTitle("Mind Diary")
                    .setContentText("Would you like to make your daily entry?")
                    .setTicker("Create Entry for Mind Diary")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(pIntent)
                    .setSound(sound)
                    .setAutoCancel(true)
                            //.addAction(0, "Load Website", pIntent)
                    .build();
            mNM.notify(1, notification);
        }
    }
}