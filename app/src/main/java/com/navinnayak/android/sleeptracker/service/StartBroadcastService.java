package com.navinnayak.android.sleeptracker.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.navinnayak.android.sleeptracker.R;
import com.navinnayak.android.sleeptracker.activity.MainActivity;
import com.navinnayak.android.sleeptracker.receiver.ScreenStatusReceiver;

import static com.navinnayak.android.sleeptracker.AppConstants.NOTIFICATION_CHANNEL_ID;
import static com.navinnayak.android.sleeptracker.AppConstants.NOTIFICATION_ID;
import static com.navinnayak.android.sleeptracker.AppConstants.NOTIFICATION_TEXT;

public class StartBroadcastService extends Service {

    private static final String TAG = StartBroadcastService.class.getCanonicalName();

    @Override
    public void onCreate() {
        super.onCreate();

        ScreenStatusReceiver receiver = new ScreenStatusReceiver();
        registerReceiver(receiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            ScreenStatusReceiver userPresent = new ScreenStatusReceiver();
            registerReceiver(userPresent, new IntentFilter(Intent.ACTION_USER_PRESENT));

            showNotification();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Method which shows notification while the service is active
     * Notifications shown for devices with Android version Oreo and above
     **/
    private void showNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(StartBroadcastService.this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(NOTIFICATION_TEXT)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(NOTIFICATION_ID, notification);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_ID, notification);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d(TAG, "onTaskRemoved: SBS ");
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
    }
}