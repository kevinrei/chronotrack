package com.kevinrei.chronotrack;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class AlarmController {
    final int SDK_INT = Build.VERSION.SDK_INT;

    public void setAlarm(Context context, Alarm alarm) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (SDK_INT < Build.VERSION_CODES.KITKAT) {
            am.set(AlarmManager.RTC_WAKEUP, alarm.getCountdown(), getPendingIntent(context, alarm));
        } else if (SDK_INT >= Build.VERSION_CODES.KITKAT && SDK_INT < Build.VERSION_CODES.M) {
            am.setExact(AlarmManager.RTC_WAKEUP, alarm.getCountdown(),
                    getPendingIntent(context, alarm));
        } else if (SDK_INT >= Build.VERSION_CODES.M) {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarm.getCountdown(),
                    getPendingIntent(context, alarm));
        }
    }

    public void cancelAlarm(Context context, Alarm alarm) {
        PendingIntent pi = getPendingIntent(context, alarm);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        am.cancel(pi);
        pi.cancel();
    }

    PendingIntent getPendingIntent(Context context, Alarm alarm) {
        Intent i = new Intent(context, AlarmReceiver.class).putExtra("alarm", alarm);
        return PendingIntent.getBroadcast(context, alarm.getAlarmId(), i,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
