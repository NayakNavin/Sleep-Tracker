package com.navinnayak.android.sleeptracker.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.navinnayak.android.sleeptracker.data.SleepContract.SleepEntry;


public class SleepDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "dizzy.db";
    private static final int DATABASE_VERSION = 1;

    public SleepDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        String SQL_CREATE_SLEEP_TABLE = " CREATE TABLE " + SleepEntry.TABLE_NAME + "("
                + SleepEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + SleepEntry.COLUMN_SLEEP_START_TIME + " TEXT, "
                + SleepEntry.COLUMN_SLEEP_END_TIME + " TEXT  , "
                + SleepEntry.COLUMN_LAST_UPDATED + " LONG ); ";
        db.execSQL(SQL_CREATE_SLEEP_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + SleepEntry.TABLE_NAME);
    }
}
