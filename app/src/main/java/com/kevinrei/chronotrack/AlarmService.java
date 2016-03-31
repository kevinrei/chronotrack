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
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

public class AlarmService extends Service {

    private NotificationManager mNotificationManager;
    private MySQLiteHelper db;

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

        db = new MySQLiteHelper(this);
        Alarm alarm = null;
        int save = -1;

        if (intent != null) {
            alarm = intent.getParcelableExtra("alarm");
            save = intent.getIntExtra("save", 0);
        }

        if (alarm != null) {
            Log.d("alarmId", String.valueOf(alarm.getAlarmId()));
        } else {
            Log.d("alarmId", "Alarm is null");
        }
        Log.d("save", String.valueOf(save));

        mBuilder.setContentTitle("ChronoTrack");
        mBuilder.setContentText("Ready to rumble!!");
        mBuilder.setTicker("Stamina notification");
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
        mBuilder.setSound(uri);
        mBuilder.setLights(Color.BLUE, 3000, 3000);

/*        if (alarmId != -1) {
            final Alarm alarm = db.getAlarm(alarmId);

            // Cancel alarm, just to be safe
            AddAlarmActivity.cancelAlarm(alarm.getAlarmId());

            // Remove alarm from active list
            removeFromActiveList(alarm);

            // Alarm isn't saved, so delete it
            if (save == 0) {
                db.deleteAlarm(alarm);
            }
        }*/

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


    /** Custom methods */

    private void removeFromActiveList(final Alarm alarm) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int position = -1;

                for (Alarm a : AlarmAdapter.alarms) {
                    int id = a.getAlarmId();
                    if (id == alarm.getAlarmId()) {
                        position = AlarmAdapter.alarms.indexOf(a);
                    }
                }

                AlarmAdapter.alarms.remove(position);
                AlarmListFragment.mAlarmAdapter.notifyItemRemoved(position);
                AlarmListFragment.mAlarmAdapter.notifyItemRangeChanged(0, AlarmAdapter.alarms.size());
            }
        }, 5000);
    }
}