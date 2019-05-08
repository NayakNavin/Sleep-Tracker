package com.navinnayak.android.sleeptracker;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.navinnayak.android.sleeptracker.data.SleepContract.SleepEntry;
import com.yinglan.circleviewlibrary.CircleAlarmTimerView;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "MainActivity";

    SleepCursorAdapter mCursorAdapter;

    long startTimeMilli;
    long endTimeMilli;
    private CircleAlarmTimerView circleAlarmTimerView;
    private TextView startTimeTV;
    private TextView endTimeTV;
    Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent backgroundService = new Intent(getApplicationContext(), StartBroadcastService.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(backgroundService);
        } else {
            startService(backgroundService);
        }

        ListView sleepLV = findViewById(R.id.list);

        mCursorAdapter = new SleepCursorAdapter(this, null);
        sleepLV.setAdapter(mCursorAdapter);

        getSupportLoaderManager().initLoader(0, null, this);


    }

//    public void init() {
//        startTimeTV = findViewById(R.id.start);
//        endTimeTV = findViewById(R.id.end);
//
//        circleAlarmTimerView = findViewById(R.id.circletimerview);
//        circleAlarmTimerView.setOnTimeChangedListener(new CircleAlarmTimerView.OnTimeChangedListener() {
//            @Override
//            public void start(String starting) {
//                startTimeTV.setText(starting);
//            }
//
//            @Override
//            public void end(String ending) {
//                endTimeTV.setText(ending);
//            }
//        });
//    }
//
//    public void startTimeToMilli() {
//        String startSubString1 = startTimeTV.getText().toString().substring(0, 2);
//        String startSubString2 = startTimeTV.getText().toString().substring(3);
//        long startHour = Long.parseLong(startSubString1);
//        long startMinute = Long.parseLong(startSubString2);
//        startTimeMilli = (startHour * 3600000) + (startMinute * 60000);
//
//
//    }
//
//    public void endTimeToMilli() {
//        String endSubString1 = endTimeTV.getText().toString().substring(0, 2);
//        String endSubString2 = endTimeTV.getText().toString().substring(3);
//
//        long endHour = Long.parseLong(endSubString1);
//        long endMinute = Long.parseLong(endSubString2);
//        endTimeMilli = (endHour * 3600000) + (endMinute * 60000);
//
//    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public Loader onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                SleepEntry._ID,
                SleepEntry.COLUMN_SLEEP_START_TIME,
                SleepEntry.COLUMN_SLEEP_END_TIME
        };
        return new CursorLoader(this,
                SleepEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);


    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);

    }

    public void deleteAllEntries() {
        int rowsDeleted = getContentResolver().delete(SleepEntry.CONTENT_URI, null, null);
        Log.d("MainActivity.", rowsDeleted + " rows deleted from sleep database");

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                deleteAllEntries();
                return true;
            case R.id.targetTime:
                toSetTargetActivity();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void toSetTargetActivity() {
        Intent intent = new Intent(MainActivity.this, SetTargetActivity.class);
        startActivity(intent);
    }


    public void scheduleJob(View v) {
        ComponentName componentName = new ComponentName(this, SleepService.class);
        JobInfo info = new JobInfo.Builder(123, componentName)
//                .setRequiresCharging(true)
//                .setRequiresDeviceIdle(true)
                .setPersisted(true)
                .setPeriodic(DateUtils.DAY_IN_MILLIS)
                .build();

        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        int resultCode = scheduler.schedule(info);
        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            Log.d(TAG, "Job scheduled");
        } else {
            Log.d(TAG, "Job scheduling failed");
        }
    }

    public void cancelJob(View v) {
        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        scheduler.cancel(123);
        Log.d(TAG, "Job cancelled");
    }
}