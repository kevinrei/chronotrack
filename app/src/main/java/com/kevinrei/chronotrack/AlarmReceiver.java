package com.kevinrei.chronotrack;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        WakefulIntentService.acquireStaticLock(context);

        Alarm alarm = intent.getParcelableExtra("alarm");
        sendNotification(context, alarm);
        context.startService(new Intent(context, AlarmService.class));
    }

    public void sendNotification(Context context, Alarm alarm) {
        NotificationManager mNotificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Based on user preference
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);

        // Build the notification
        mBuilder.setContentIntent(getPendingIntent(context, alarm.getAlarmId()));
        mBuilder.setContentTitle("ChronoTrack");
        mBuilder.setContentText(alarm.getLabel());
        mBuilder.setLights(Color.BLUE, 3000, 3000);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setSound(uri);
        mBuilder.setTicker("ChronoTrack alarm triggered!");
        mBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
        mBuilder.setWhen(System.currentTimeMillis());

        Notification mNotification = mBuilder.build();
        mNotificationManager.notify(alarm.getId(), mNotification);
    }

    PendingIntent getPendingIntent(Context context, int alarmId) {
        Intent i = new Intent(context, AlarmReceiver.class).putExtra("alarm_id", alarmId);
        return PendingIntent.getBroadcast(context, alarmId, i, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
