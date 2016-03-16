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

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        DataBaseHelper dbh = new DataBaseHelper(context);
        Log.d("alarm  :  " + context.getApplicationContext().getPackageResourcePath(), "   alarm");
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
/*
    public static void alarmNotification(Context context){
        //Get alarm manager service
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        //Create pending intent for receiver
        Intent intent = new Intent(context, NotificationHelper.class);
        PendingIntent pendingIntentReceiver = PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);


    }


    @SuppressLint("NewApi")
    public static void alarmNotification1(Context context) {
        //Create notification
        Notification.Builder notification =
                new Notification.Builder(
                        context.getApplicationContext())
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Mind Diary")
                        .setContentText("You have not yet made your daily entry"
                                + "\nWould you like to make one, now?")
                        .setAutoCancel(true);

        Intent intent = new Intent(context, InputScreen.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(pendingIntent);


        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification.build());

        //Schedule notification

    }
}
*/