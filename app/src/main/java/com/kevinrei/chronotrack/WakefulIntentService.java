package com.kevinrei.chronotrack;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

public class WakefulIntentService extends IntentService {
    public static final String LOCK_NAME_STATIC = "com.kevinrei.chronotrack.AlarmService.Static";
    public static final String LOCK_NAME_LOCAL = "com.kevinrei.chronotrack.AlarmService.Local";

    private static PowerManager.WakeLock lockStatic = null;
    private static PowerManager.WakeLock lockLocal = null;

    public WakefulIntentService(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        lockLocal = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, LOCK_NAME_LOCAL);
        lockLocal.setReferenceCounted(true);
    }

    @Override
    public void onStart(Intent intent, final int startId) {
        lockLocal.acquire();
        super.onStart(intent, startId);
        getLock(this).release();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        lockLocal.release();
    }

    //  Acquire a partial static WakeLock, called within the class that calls startService()
    public static void acquireStaticLock(Context context) {
        getLock(context).acquire();
    }

    private static synchronized PowerManager.WakeLock getLock(Context context) {
        if (lockStatic == null) {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            lockStatic = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, LOCK_NAME_STATIC);
            lockStatic.setReferenceCounted(true);
        }

        return lockStatic;
    }
}
