package com.navinnayak.android.sleeptracker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.IBinder;


public class SleepService extends Service {
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Query the database and show alarm if it applies

        // Here you can return one of some different constants.
        // This one in particular means that if for some reason
        // this service is killed, we don't want to start it
        // again automatically

        beginAppInBackground();
        return START_NOT_STICKY;
    }

    /**
     * method to schedule the app to run at scheduled time
     **/
    public void beginAppInBackground() {
        alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, SleepService.class);
        alarmIntent = PendingIntent.getService(this, 0, intent, 0);


        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 11);
        calendar.set(Calendar.MINUTE, 00);


//// With setInexactRepeating(), you have to use one of the AlarmManager interval
//// constants--in this case, AlarmManager.INTERVAL_DAY.
        alarmMgr.setInexactRepeating(AlarmManager.RTC, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent);

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


    }
}