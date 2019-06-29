package com.navinnayak.android.sleeptracker.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.navinnayak.android.sleeptracker.service.StartBroadcastService;

public class BootDeviceReceiver extends BroadcastReceiver {
    private static final String TAG = BootDeviceReceiver.class.getCanonicalName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Log.d(TAG, "on boot complete");

            Intent serviceIntent = new Intent(context, StartBroadcastService.class);
            serviceIntent.putExtra("UserAction", intent.getAction());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.startForegroundService(context, serviceIntent);
            } else {
                context.startService(serviceIntent);
            }
        }
    }
}