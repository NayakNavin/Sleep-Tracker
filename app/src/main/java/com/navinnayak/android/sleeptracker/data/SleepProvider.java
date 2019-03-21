package com.navinnayak.android.sleeptracker.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.navinnayak.android.sleeptracker.data.SleepContract.SleepEntry;

public class SleepProvider extends ContentProvider {

    private static final int SLEEPS = 100;
    private static final int SLEEP_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(SleepContract.CONTENT_AUTHORITY, SleepContract.PATH_SLEEP, SLEEPS);
        sUriMatcher.addURI(SleepContract.CONTENT_AUTHORITY, SleepContract.PATH_SLEEP + "/#", SLEEP_ID);
    }

    private SleepDbHelper mDbHelper;


    @Override
    public boolean onCreate() {
        mDbHelper = new SleepDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case SLEEPS:
                cursor = database.query(SleepEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case SLEEP_ID:
                selection = SleepEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(SleepEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Cannot query unknown Uri" + uri);

        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;

    }


    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {

            case SLEEPS:
                return insertSleep(uri, contentValues);

            default:
                throw new IllegalArgumentException("Insertion is not supported for" + uri);


        }


    }

    private Uri insertSleep(Uri uri, ContentValues values) {
        values.getAsString(SleepEntry.COLUMN_SLEEP_START_TIME);
        values.getAsString(SleepEntry.COLUMN_SLEEP_END_TIME);

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id = database.insert(SleepEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.v("message:", "Failed to insert new row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {

        final int match = sUriMatcher.match(uri);
        switch (match) {

            case SLEEPS:
                return updateSleep(uri, contentValues, selection, selectionArgs);

            case SLEEP_ID:
                selection = SleepEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateSleep(uri, contentValues, selection, selectionArgs);

            default:
                throw new IllegalArgumentException("Update is not supported for" + uri);
        }
    }

    private int updateSleep(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(SleepEntry.COLUMN_SLEEP_START_TIME)) {
            String startTime = values.getAsString(SleepEntry.COLUMN_SLEEP_START_TIME);
            if (startTime == null) {
                throw new IllegalArgumentException("start time required");
            }
        }
        if (values.containsKey(SleepEntry.COLUMN_SLEEP_END_TIME)) {
            String endTime = values.getAsString(SleepEntry.COLUMN_SLEEP_END_TIME);
            if (endTime == null) {
                throw new IllegalArgumentException("end time required");
            }
        }


        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsUpdated = database.update(SleepEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;


    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsDeleted;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case SLEEPS:
                rowsDeleted = database.delete(SleepEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case SLEEP_ID:
                selection = SleepEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(SleepEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for" + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;

    }

    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);
        switch (match) {

            case SLEEPS:
                return SleepEntry.CONTENT_LIST_TYPE;

            case SLEEP_ID:
                return SleepEntry.CONTENT_ITEM_TYPE;

            default:
                throw new IllegalArgumentException("Unknown Uri" + uri + "with match" + match);
        }
    }
}
