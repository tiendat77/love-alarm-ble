package com.dathuynh.plugins.love_alarm_ble.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.dathuynh.plugins.love_alarm_ble.LoveAlarmBlePlugin;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Notify {

    public static final String CHANNEL_ID = "79bb-bd53";
    public static final String CHANNEL_NAME = "Love Alarm";

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void initialize(Context context) {
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
        );

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);
    }

    public static void show(Context context, String title, String message) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(getResourceID(context, "ic_notification"))
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true);

        Log.d("CMN", "Show notification");
        manager.notify(genID(), builder.build());
    }

    public static int genID(){
        Date now = new Date();
        String format = new SimpleDateFormat("ddHHmmss",  Locale.US).format(now);
        return Integer.parseInt(format);
    }

    public static int getResourceID(Context context, String resourceName) {
        int resId = 0;

        try {
            resId = context.getResources().getIdentifier(resourceName, "drawable", context.getPackageName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resId;
    }

    public static void showNotification(Context context, String title, String body) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        int notificationId = 1;
        String channelId = "channel-01";
        String channelName = "Channel Name";
        int importance = NotificationManager.IMPORTANCE_HIGH;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(getResourceID(context, "ic_notification"))
                .setContentTitle(title)
                .setContentText(body);

//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
//        stackBuilder.addNextIntent(intent);
//        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
//                0,
//                PendingIntent.FLAG_UPDATE_CURRENT
//        );
//        mBuilder.setContentIntent(resultPendingIntent);

        notificationManager.notify(notificationId, mBuilder.build());
    }
}
