package com.navinnayak.android.sleeptracker.service;

import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.icu.util.Calendar;
import android.net.Uri;
import android.support.annotation.MainThread;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.navinnayak.android.sleeptracker.R;
import com.navinnayak.android.sleeptracker.activity.MainActivity;
import com.navinnayak.android.sleeptracker.data.SleepContract;
import com.navinnayak.android.sleeptracker.data.SleepContract.SleepEntry;

import static android.content.Intent.ACTION_USER_PRESENT;
import static com.navinnayak.android.sleeptracker.AppConstants.NOTIFICATION_CHANNEL_ID;
import static com.navinnayak.android.sleeptracker.AppConstants.NOTIFICATION_ID;
import static com.navinnayak.android.sleeptracker.activity.SetTargetActivity.END_TIME;
import static com.navinnayak.android.sleeptracker.activity.SetTargetActivity.SHARED_PREF;
import static com.navinnayak.android.sleeptracker.activity.SetTargetActivity.START_TIME;
import static com.navinnayak.android.sleeptracker.data.SleepContract.BASE_CONTENT_URI;
import static com.navinnayak.android.sleeptracker.util.Util.getMillisOfDate;

public class SleepJobService extends JobService implements SensorEventListener {

    private static final String TAG = SleepJobService.class.getCanonicalName();
    private static final int SHAKE_THRESHOLD = 200;
    private boolean jobCancelled = true;
    private String actionToPerform;
    private Uri mCurrentSleepUri;
    private long sleepStartTime;
    private long startPrefLong;
    private long endPrefLong;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private long lastUpdate = 0;
    private float lastX, lastY, lastZ;
    private boolean shake;
    private long actualStartMillis;
    private long actualEndMillis;

    private long currentTimeInMillis = System.currentTimeMillis();
    private boolean threadSleep = true;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        actionToPerform = intent.getExtras().getString("UserAction");

        SharedPreferences preferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        startPrefLong = preferences.getLong(START_TIME, 0);
        endPrefLong = preferences.getLong(END_TIME, 0);

//        Log.d(TAG, "Got startTime as " + startPrefLong + " and end time as " + endPrefLong + " from Shared preferences");
//        Log.d(TAG, actionToPerform);

        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        showNotification();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "Job started");
        doBackgroundWork(params);
        return true;

    }

    private void doBackgroundWork(final JobParameters params) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                calculateSleep();
                if (currentTimeInMillis > actualStartMillis && currentTimeInMillis < actualEndMillis) {
                    if (!shake && !isDeviceLocked() && !isScreenOff()) {
                        threadSleep = false;

                        try {
                            Log.d(TAG, "Thread is SLEEPING: ");
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (threadSleep) {
                    startTimeRecord();
                }
                endTimeRecord();
            }
        }).start();

        if (jobCancelled) {
            return;
        }

        Log.d(TAG, "Job finished");
        jobFinished(params, false);
    }

    /**
     * Method which calculates sleep time.
     **/
    public void calculateSleep() {
        Calendar currentTime = Calendar.getInstance();

//        Log.d("time", String.valueOf(currentTime.get(Calendar.HOUR_OF_DAY)));
        String dateString = String.format("%02d-%02d-%04d", currentTime.get(Calendar.DATE), currentTime.get(Calendar.MONTH) + 1, currentTime.get(Calendar.YEAR));
        long currentDayAt12AM = getMillisOfDate(dateString, null);
        long nextDayAt12AM = currentDayAt12AM + 1000 * 60 * 60 * 24;
        actualStartMillis = startPrefLong + currentDayAt12AM;
        actualEndMillis = endPrefLong < startPrefLong ? endPrefLong + nextDayAt12AM : endPrefLong + currentDayAt12AM;
//        Log.d(TAG, "**" + currentTimeInMillis + "** " + actualStartMillis + " - " + actualEndMillis + ", " + startPrefLong + " - " + endPrefLong);

        if (currentTimeInMillis < actualStartMillis && currentTimeInMillis > actualEndMillis) {
            Log.d(TAG, "Service not started since its not the time to start it :P");
            stopForeground(true);
            onDestroy();
        }
    }

    public void startTimeRecord() {
        if (currentTimeInMillis > actualStartMillis && currentTimeInMillis < actualEndMillis) {
            if (!shake) {
                if (isDeviceLocked() && isScreenOff()) {
                    Log.d(TAG, "Service  started  and start time ");
                    recordStartTime();
                }
            }
        }
    }

    public void endTimeRecord() {
        if (currentTimeInMillis > actualStartMillis && currentTimeInMillis < actualEndMillis + (1 * 60 * 60 * 1000)) {
            if (!isDeviceLocked() && !isScreenOff() && shake) {
                threadSleep = true;
                recordEndTime();
                Log.d(TAG, "Service  end time ");
            }

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
            long sleepLastUpdated = System.currentTimeMillis();

            Log.d(TAG, "Service  end time is " + sleepEndTime);
            Log.d(TAG, "Going to append entry with id: " + latestEntryId);

            values.put(SleepEntry.COLUMN_SLEEP_END_TIME, sleepEndTime);
            values.put(SleepEntry.COLUMN_LAST_UPDATED, sleepLastUpdated);
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
        if (keyguardManager.isDeviceLocked() || (actionToPerform != null && !actionToPerform.equals(ACTION_USER_PRESENT))) {

            Log.d(TAG, "locked with keyguard");
            return true;

        } else {
            Log.d(TAG, "unlocked with keyguard");
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

    @Override
    public boolean onStopJob(JobParameters params) {
        jobCancelled = true;
        return false;
    }

    private void showNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(SleepJobService.this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Sleep")
                .setContentText("Your Sleep will be tracked.")
                .setContentIntent(pendingIntent)
                .build();
        startForeground(NOTIFICATION_ID, notification);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_ID, notification);
    }

    /**
     * Method which shows notification while the service is active
     * Notifications shown for devices with Android version Oreo and above
     **/
    @Override
    @MainThread
    public void onTaskRemoved(Intent rootIntent) {
        Log.d(TAG, "onTaskRemoved: ");
        super.onTaskRemoved(rootIntent);
    }

    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            long currentTime = System.currentTimeMillis();

            if ((currentTime - lastUpdate) > 100) {
                long diffTime = (currentTime - lastUpdate);
                lastUpdate = currentTime;

                float speed = Math.abs(x + y + z - lastX - lastY - lastZ) / diffTime * 10000;

                shake = speed > SHAKE_THRESHOLD;
                lastX = x;
                lastY = y;
                lastZ = z;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}