package com.navinnayak.android.sleeptracker;

import android.app.KeyguardManager;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.navinnayak.android.sleeptracker.data.SleepContract.SleepEntry;

public class MainActivity extends AppCompatActivity {
    private long deviceSleepTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    /**
     * method to check device locked or not
     **/
    public boolean checkDeviceLock() {
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        if (keyguardManager.isDeviceLocked() == true) {

            //it is locked
            deviceSleepTime = System.currentTimeMillis();

            return true;
        } else {
            deviceSleepTime = System.currentTimeMillis();

            return false;
            //it is not locked
        }


    }


    /**
     * method to check device idle or not
     **/
    public boolean checkDeviceIdle() {
        long minSleepTime = deviceSleepTime + 3600000;
        return deviceSleepTime > minSleepTime;
    }

    public void saveTime() {
        ContentValues values = new ContentValues();

        if (checkDeviceLock() == true && checkDeviceIdle() == true) {
            values.put(SleepEntry.COLUMN_SLEEP_START_TIME, deviceSleepTime);
        }


        if (checkDeviceIdle() == true && deviceWaketime > deviceSleepTime) {
            values.put(SleepEntry.COLUMN_SLEEP_END_TIME,deviceWakeTime);

        }


        Uri newUri = getContentResolver().insert(SleepEntry.CONTENT_URI, values);

    }


}