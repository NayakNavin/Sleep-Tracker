package com.navinnayak.android.sleeptracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class SleepBroadcast extends BroadcastReceiver {
    public long deviceWakeTime = 0;



    @Override
    public void onReceive(Context context, Intent intent) {
        /*Sent when the user is present after
         * device wakes up (e.g when the keyguard is gone)
         * */
        if(intent.getAction().equals(Intent.ACTION_USER_PRESENT))
        {
            initializeWakeTime();
        }

    }
    public void initializeWakeTime(){

        deviceWakeTime = System.currentTimeMillis();

    }



}
