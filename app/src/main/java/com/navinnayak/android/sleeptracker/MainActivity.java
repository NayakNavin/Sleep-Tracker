package com.navinnayak.android.sleeptracker;

import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.navinnayak.android.sleeptracker.data.SleepContract.SleepEntry;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = MainActivity.class.getCanonicalName();
    SleepCursorAdapter mCursorAdapter;
    private ScreenStatusReceiver userPresent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent backgroundService = new Intent(getApplicationContext(), StartBroadcastService.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            userPresent = new ScreenStatusReceiver();
            registerReceiver(userPresent, new IntentFilter(Intent.ACTION_USER_PRESENT));

            startForegroundService(backgroundService);
        } else {
            startService(backgroundService);
        }

        ListView sleepLV = findViewById(R.id.list);
        mCursorAdapter = new SleepCursorAdapter(this, null);
        sleepLV.setAdapter(mCursorAdapter);
        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(userPresent);
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
        Log.d(TAG, rowsDeleted + " rows deleted from sleep database");
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
}