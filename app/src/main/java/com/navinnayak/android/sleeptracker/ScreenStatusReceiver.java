package com.navinnayak.android.sleeptracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.Log;


public class ScreenStatusReceiver extends BroadcastReceiver {
    private static final String TAG = ScreenStatusReceiver.class.getCanonicalName();

    @Override
    public void onReceive(Context context, Intent intent) {
        /*Sent when the user is present after
         * device wakes up (e.g when the keyguard is gone)
         * */

        if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
            Log.d(TAG, "Action performed" + intent.getAction());
        }

        Intent serviceIntent = new Intent(context, SleepService.class);
        serviceIntent.putExtra("UserAction", intent.getAction());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(context, serviceIntent);
        } else {
            context.startService(serviceIntent);
        }


    }


}
