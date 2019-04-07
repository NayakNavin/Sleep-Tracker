package com.navinnayak.android.sleeptracker;

import android.app.KeyguardManager;
import android.app.Notification;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.fingerprint.FingerprintManager;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.navinnayak.android.sleeptracker.data.SleepContract;
import com.navinnayak.android.sleeptracker.data.SleepContract.SleepEntry;

import static com.navinnayak.android.sleeptracker.AppConstants.NOTIFICATION_CHANNEL_ID;
import static com.navinnayak.android.sleeptracker.AppConstants.NOTIFICATION_ID;
import static com.navinnayak.android.sleeptracker.data.SleepContract.BASE_CONTENT_URI;

public class SleepService extends Service {
    private static final String TAG = SleepService.class.getCanonicalName();
    private boolean check = false;
    private String actionToPerform;
    private Uri mCurrentSleepUri;
    private long sleepStartTime;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        actionToPerform = intent.getExtras().getString("UserAction");
        Log.d(TAG, actionToPerform);
        calculateSleep();
        stopService();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            showNotification();
            return START_STICKY;

        } else {
            return START_NOT_STICKY;
        }
    }

    /**
     * Method which calculates sleep time.
     **/
    public void calculateSleep() {
        Calendar currentTime = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();

        currentTime.get(Calendar.HOUR_OF_DAY);
        currentTime.get(Calendar.MINUTE);
        int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);

        calendar.set(Calendar.HOUR_OF_DAY, 21);
        calendar.set(Calendar.MINUTE, 0);
        int fixedHour = calendar.get(Calendar.HOUR_OF_DAY);

        int minSleepTime = 3600000;

        Log.d("time", String.valueOf(currentTime.get(Calendar.HOUR_OF_DAY)));
//        if (currentHour >= fixedHour) {
        if (isDeviceLocked() && isScreenOff()) {
            Log.d(TAG, "Service  started  and start time");
            recordStartTime();
//                sleepStartTime = System.currentTimeMillis();
        } else if (!isDeviceLocked() || isBiometricsOn()) {
//                if (System.currentTimeMillis() > sleepStartTime + minSleepTime) {
//                    recordStartTime();
            recordEndTime();
            Log.d(TAG, "Service  end time ");
//                }
//            }
        } else {
            Log.d(TAG, "Service not started since its not the time to start it :P");
            stopForeground(true);
            onDestroy();
        }
    }

    /**
     * Method to record Sleep Start Time
     * and insert into the database.
     **/
    public void recordStartTime() {
        ContentValues values = new ContentValues();
        sleepStartTime = System.currentTimeMillis();
        long sleepLastUpdated = System.currentTimeMillis();

        values.put(SleepEntry.COLUMN_LAST_UPDATED, sleepLastUpdated);
        values.put(SleepEntry.COLUMN_SLEEP_START_TIME, sleepStartTime);

        if (mCurrentSleepUri == null) {
            Uri newUri = getContentResolver().insert(SleepEntry.CONTENT_URI, values);
            if (newUri == null) {
                Log.d(TAG, "insert failed");

            } else {
                Log.d(TAG, "insert successful");
            }
        }
    }

    /**
     * Method to record Sleep End Time
     * and update into the database
     * with corresponding ID
     **/
    public void recordEndTime() {
        ContentValues values = new ContentValues();
        Cursor latestEntry = getContentResolver().query(SleepEntry.CONTENT_URI, new String[]{SleepEntry._ID}, null, null, "last_updated DESC LIMIT 1");
        if (latestEntry.getCount() > 0 && latestEntry.moveToLast()) {
            int latestEntryColumnIndex = latestEntry.getColumnIndex(SleepEntry._ID);
            int latestEntryId = latestEntry.getInt(latestEntryColumnIndex);
            long sleepEndTime = System.currentTimeMillis();

            Log.d(TAG, "Service  end time is " + sleepEndTime);
            Log.d(TAG, "Going to append entry with id" + latestEntryId);

            values.put(SleepEntry.COLUMN_SLEEP_END_TIME, sleepEndTime);
            Uri latestEntryUri = BASE_CONTENT_URI.buildUpon().appendPath(SleepContract.PATH_SLEEP).appendPath(String.valueOf(latestEntryId)).build();

            int rowsAffected = getContentResolver().update(latestEntryUri, values, null, null);
            if (rowsAffected == 0) {
                Log.d(TAG, "update failed");

            } else {
                Log.d(TAG, "update successful");
            }
        }
    }

    /**
     * Method to check if the device is locked/unlocked with keyguard.
     * (pin, password, or pattern )
     **/
    public boolean isDeviceLocked() {
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        if (keyguardManager.isDeviceLocked()) {

            //it is locked
            Log.d(TAG, "locked with keyguard");
            return true;

        } else {
            //it is not locked
            Log.d(TAG, "unlocked with keyguard");
            return false;
        }
    }

    /**
     * Method to check if the device is locked/unlocked with biometrics.
     * (fingerprints )
     **/
    public boolean isBiometricsOn() {
        FingerprintManager fingerprint = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
        if (fingerprint.hasEnrolledFingerprints()) {
            Log.d(TAG, "unlocked with fingerprint");
            return true;

        } else {
            Log.d(TAG, "locked with fingerprint");
            return false;
        }
    }

    /**
     * Method to which returns boolean value for Screen ON/OFF activity
     * <p>
     * returns true if Screen Off
     * returns false if Screen ON
     **/
    public boolean isScreenOff() {
        return actionToPerform.equals(Intent.ACTION_SCREEN_OFF);
    }

    public void stopService() {
        Cursor c = getContentResolver().query(SleepEntry.CONTENT_URI, null, null, null, null);

        Log.d(TAG, String.valueOf(c.getCount()));
        if (c.getCount() > 0 && c.moveToLast()) {

            // Find the columns of sleep attributes that we're interested in
            int startTimeColumnIndex = c.getColumnIndex(SleepEntry.COLUMN_SLEEP_START_TIME);
            int endTimeColumnIndex = c.getColumnIndex(SleepEntry.COLUMN_SLEEP_END_TIME);

            // Extract out the value from the Cursor for the given column index
            int startTime = c.getInt(startTimeColumnIndex);
            int endTime = c.getInt(endTimeColumnIndex);
            c.close();

            if (startTime != 0 && endTime != 0) {
                onDestroy();
                stopForeground(false);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * method to stop the service
     **/
    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
    }

    /**
     * Method which shows notification while the service is active
     * Notifications shown for devices with Android version Oreo and above
     **/
    private void showNotification() {
        Notification notification = new NotificationCompat.Builder(SleepService.this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Sleep")
                .setContentText("Service is running background")
                .build();
        startForeground(NOTIFICATION_ID, notification);
    }
}