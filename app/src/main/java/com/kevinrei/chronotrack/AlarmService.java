package com.kevinrei.chronotrack;

import android.content.Intent;
import android.util.Log;

import java.util.List;

public class AlarmService extends WakefulIntentService {

    public AlarmService() {
        super("AlarmService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        MySQLiteHelper db = new MySQLiteHelper(this);
        AlarmController controller = new AlarmController();

        List<Alarm> alarms = db.getAllAlarms();
        for (Alarm alarm : alarms) {
            controller.cancelAlarm(this, alarm);

            if (alarm.getCountdown() >= System.currentTimeMillis()) {
                controller.setAlarm(this, alarm);
            }

            else if (alarm.getSave() == 0) {
                db.deleteAlarm(alarm);
            }
        }

        super.onHandleIntent(intent);
    }
}