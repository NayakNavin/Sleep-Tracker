//package com.navinnayak.android.sleeptracker;
//
//import android.app.KeyguardManager;
//import android.app.Notification;
//import android.app.job.JobParameters;
//import android.app.job.JobService;
//import android.content.ContentValues;
//import android.content.Intent;
//import android.database.Cursor;
//import android.hardware.fingerprint.FingerprintManager;
//import android.icu.util.Calendar;
//import android.net.Uri;
//import android.os.Build;
//import android.support.v4.app.NotificationCompat;
//import android.util.Log;
//
//import com.navinnayak.android.sleeptracker.data.SleepContract;
//import com.navinnayak.android.sleeptracker.data.SleepContract.SleepEntry;
//
//import static com.navinnayak.android.sleeptracker.AppConstants.NOTIFICATION_CHANNEL_ID;
//import static com.navinnayak.android.sleeptracker.AppConstants.NOTIFICATION_ID;
//import static com.navinnayak.android.sleeptracker.data.SleepContract.BASE_CONTENT_URI;
//
//public class SleepJobService extends JobService {
//
//    private static final String TAG = "SleepJobService";
//    private boolean jobCancelled = false;
//    private boolean check = false;
//    private String actionToPerform;
//
//    private Uri mCurrentSleepUri;
//    private long sleepStartTime;
//
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        super.onStartCommand(intent, flags, startId);
//
//        actionToPerform = intent.getExtras().getString("UserAction");
//
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            showNotification();
//
//        }
//        return START_NOT_STICKY;
//    }
//
//    @Override
//    public boolean onStartJob(JobParameters params) {
//        Log.d(TAG, "Job started");
//        doBackgroundWork(params);
//
//        Log.d(TAG, actionToPerform);
//
//
//        return true;
//
//    }
//
//
//    private void doBackgroundWork(final JobParameters params) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                if (jobCancelled) {
//                    return;
//                }
//
//                calculateSleep();
////                showNotification();
//
//
//                Log.d(TAG, "Job finished");
//                jobFinished(params, false);
//            }
//        }).start();
//
//
//    }
//
//
//    /**
//     * Method which calculates sleep time.
//     **/
//    public void calculateSleep() {
//        Calendar currentTime = Calendar.getInstance();
//        Calendar calendar = Calendar.getInstance();
//
//        currentTime.get(Calendar.HOUR_OF_DAY);
//        currentTime.get(Calendar.MINUTE);
//        int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);
//
//        calendar.set(Calendar.HOUR_OF_DAY, 5);
//        calendar.set(Calendar.MINUTE, 0);
//        int fixedHour = calendar.get(Calendar.HOUR_OF_DAY);
//
//        int minSleepTime = 1 * 60 * 60 * 1000;
//
//        Log.d("time", String.valueOf(currentTime.get(Calendar.HOUR_OF_DAY)));
////        if (currentHour >= fixedHour) {
//        if (isDeviceLocked() && isScreenOff()) {
//            Log.d(TAG, "Service  started  and start time");
//            recordStartTime();
////                sleepStartTime = System.currentTimeMillis();
//        } else if (!isDeviceLocked() || isBiometricsOn()) {
////                if (System.currentTimeMillis() > sleepStartTime + minSleepTime) {
////                    recordStartTime();
//            recordEndTime();
//            Log.d(TAG, "Service  end time ");
////                }
////            }
//        } else {
//
//            Log.d(TAG, "Service not started since its not the time to start it :P");
//            stopForeground(true);
//            onDestroy();
//        }
//    }
//
//
//    /**
//     * Method to record Sleep Start Time
//     * and insert into the database.
//     **/
//    public void recordStartTime() {
//        ContentValues values = new ContentValues();
//        sleepStartTime = System.currentTimeMillis();
//        long sleepLastUpdated = System.currentTimeMillis();
//
//        values.put(SleepEntry.COLUMN_LAST_UPDATED, sleepLastUpdated);
//        values.put(SleepEntry.COLUMN_SLEEP_START_TIME, sleepStartTime);
//
//        if (mCurrentSleepUri == null) {
//            Uri newUri = getContentResolver().insert(SleepEntry.CONTENT_URI, values);
//            if (newUri == null) {
//                Log.d(TAG, "insert failed");
//
//            } else {
//                Log.d(TAG, "insert successful");
//            }
//        }
//    }
//
//    /**
//     * Method to record Sleep End Time
//     * and update into the database
//     * with corresponding ID
//     **/
//    public void recordEndTime() {
//        ContentValues values = new ContentValues();
//        Cursor latestEntry = getContentResolver().query(SleepEntry.CONTENT_URI, new String[]{SleepEntry._ID}, null, null, "last_updated DESC LIMIT 1");
//        if (latestEntry.getCount() > 0 && latestEntry.moveToLast()) {
//            int latestEntryColumnIndex = latestEntry.getColumnIndex(SleepEntry._ID);
//            int latestEntryId = latestEntry.getInt(latestEntryColumnIndex);
//            long sleepEndTime = System.currentTimeMillis();
//
//            Log.d(TAG, "Service  end time is " + sleepEndTime);
//            Log.d(TAG, "Going to append entry with id" + latestEntryId);
//
//            values.put(SleepEntry.COLUMN_SLEEP_END_TIME, sleepEndTime);
//            Uri latestEntryUri = BASE_CONTENT_URI.buildUpon().appendPath(SleepContract.PATH_SLEEP).appendPath(String.valueOf(latestEntryId)).build();
//
//            int rowsAffected = getContentResolver().update(latestEntryUri, values, null, null);
//            if (rowsAffected == 0) {
//                Log.d(TAG, "update failed");
//
//            } else {
//                Log.d(TAG, "update successful");
//            }
//        }
//    }
//
//    /**
//     * Method to check if the device is locked/unlocked with keyguard.
//     * (pin, password, or pattern )
//     **/
//    public boolean isDeviceLocked() {
//        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
//        if (keyguardManager.isDeviceLocked()) {
//
//            //it is locked
//            Log.d(TAG, "locked with keyguard");
//            return true;
//
//        } else {
//            //it is not locked
//            Log.d(TAG, "unlocked with keyguard");
//            return false;
//        }
//    }
//
//    /**
//     * Method to check if the device is locked/unlocked with biometrics.
//     * (fingerprints )
//     **/
//    public boolean isBiometricsOn() {
//        FingerprintManager fingerprint = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
//        if (fingerprint.hasEnrolledFingerprints()) {
//            Log.d(TAG, "unlocked with fingerprint");
//            return true;
//
//        } else {
//            Log.d(TAG, "locked with fingerprint");
//            return false;
//        }
//    }
//
//    /**
//     * Method to which returns boolean value for Screen ON/OFF activity
//     * <p>
//     * returns true if Screen Off
//     * returns false if Screen ON
//     **/
//    public boolean isScreenOff() {
//        return actionToPerform.equals(Intent.ACTION_SCREEN_OFF);
//    }
//
//
//    @Override
//    public boolean onStopJob(JobParameters params) {
//        Log.d(TAG, "Job cancelled before completion");
//        jobCancelled = true;
//
//        return false;
//    }
//
//    private void showNotification() {
//        Notification notification = new NotificationCompat.Builder(SleepJobService.this, NOTIFICATION_CHANNEL_ID)
//                .setSmallIcon(R.drawable.ic_launcher_foreground)
//                .setContentTitle("Sleep")
//                .setContentText("Service is running background")
//                .build();
//        startForeground(NOTIFICATION_ID, notification);
//    }
//}
