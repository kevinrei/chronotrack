package com.kevinrei.chronotrack;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Kevin on 4/1/2016.
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        WakefulIntentService.acquireStaticLock(context);    // Acquire a partial WakeLock
        context.startService(new Intent(context, AlarmService.class)); // Start AlarmService
    }
}
