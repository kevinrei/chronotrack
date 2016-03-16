package com.kevinrei.chronotrack;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;

public class AlarmService extends Service {

    private NotificationManager mNotificationManager;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @SuppressWarnings("static-access")
    @Override
    public void onStart(Intent intent, int startId) {
        mNotificationManager = (NotificationManager) this.getApplicationContext()
                .getSystemService(this.getApplicationContext().NOTIFICATION_SERVICE);

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this.getApplicationContext());

        mBuilder.setContentTitle("ChronoTrack");
        mBuilder.setContentText("Ready to rumble!!");
        mBuilder.setTicker("Stamina notification");
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
        mBuilder.setSound(uri);
        mBuilder.setLights(Color.BLUE, 3000, 3000);

        Intent resultIntent = new Intent(this.getApplicationContext(), MainActivity.class);

        TaskStackBuilder mTaskStackBuilder = TaskStackBuilder.create(this);
        mTaskStackBuilder.addParentStack(MainActivity.class);
        mTaskStackBuilder.addNextIntent(resultIntent);

        PendingIntent mPendingIntent = mTaskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_ONE_SHOT);
        mBuilder.setContentIntent(mPendingIntent);

        Notification mNotification = mBuilder.build();
        mNotification.flags |= Notification.FLAG_AUTO_CANCEL;

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mNotification);
    }
}