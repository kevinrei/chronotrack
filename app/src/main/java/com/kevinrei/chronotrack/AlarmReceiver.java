package com.kevinrei.chronotrack;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
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

        // Get user preference
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean sound = mPrefs.getBoolean("notification_sound", true);
        boolean vibrate = mPrefs.getBoolean("notification_vibrate", true);
        int color = getColor(mPrefs.getString("notification_lights", "Blue"));
        String ringtone = mPrefs.getString("notification_ringtone", "default_ringtone");

        Uri uri;
        if (ringtone.equals("default_ringtone")) {
            uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        } else {
            uri = Uri.parse(ringtone);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        MySQLiteHelper db = new MySQLiteHelper(context);

        // Build the notification
        mBuilder.setContentIntent(getPendingIntent(context, alarm));
        mBuilder.setContentTitle(db.getGame(alarm.getGameId()).getTitle() + " alarm triggered!");
        mBuilder.setContentText(alarm.getLabel());
        mBuilder.setLights(color, 3000, 3000);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setTicker("ChronoTrack alarm triggered");
        mBuilder.setWhen(System.currentTimeMillis());

        if (vibrate) {
            mBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
        }

        if (sound) {
            mBuilder.setSound(uri);
        }

        Notification mNotification = mBuilder.build();
        mNotificationManager.notify(alarm.getId(), mNotification);
    }

    PendingIntent getPendingIntent(Context context, Alarm alarm) {
        Intent i = new Intent(context, AlarmReceiver.class).putExtra("alarm", alarm);
        return PendingIntent.getBroadcast(context, alarm.getAlarmId(), i,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private int getColor(String color) {
        switch (color) {
            case "Blue":
                return Color.BLUE;
            case "Green":
                return Color.GREEN;
            case "Red":
                return Color.RED;
            case "Yellow":
                return Color.YELLOW;
            case "White":
                return Color.WHITE;
        }

        return Color.BLUE;
    }
}
