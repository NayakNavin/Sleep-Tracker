package com.navinnayak.android.sleeptracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;


public class ScreenStatusReceiver extends BroadcastReceiver {
    private static final String TAG = ScreenStatusReceiver.class.getCanonicalName();

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(TAG, "Got Action " + intent.getAction());
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            Log.d(TAG, "Action performed" + intent.getAction());
        }

        Util.scheduleJob(context);

        Intent serviceIntent = new Intent(context, SleepJobService.class);
        serviceIntent.putExtra("UserAction", intent.getAction());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent);
        } else {
            context.startService(serviceIntent);
        }
    }
}