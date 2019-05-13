package com.navinnayak.android.sleeptracker;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.icu.util.Calendar;
import android.icu.util.TimeZone;
import android.support.annotation.Nullable;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Util {

    private static final String TAG = Util.class.getSimpleName();

    public static void scheduleJob(Context context) {
        ComponentName componentName = new ComponentName(context, SleepJobService.class);
        JobInfo info = new JobInfo.Builder(123, componentName)
                .setPersisted(true)
                .setPeriodic(12 * 60 * 60 * 1000)
                .build();

        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        int resultCode = scheduler.schedule(info);
        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            Log.d(TAG, "Job scheduled");
        } else {
            Log.d(TAG, "Job scheduling failed");
        }
    }

    /**
     * Returns the milliseconds for the given date string
     *
     * @param dateString The datestring for which you need it in milliseconds
     * @param format     Format of the date. If not given it assumes the format dd-M-yyyy
     * @return milliseconds
     */
    public static long getMillisOfDate(String dateString, @Nullable String format) {
        if (format == null || format.isEmpty()) {
            format = "dd-M-yyyy";
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        try {
            return simpleDateFormat.parse(dateString).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));

        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }
}