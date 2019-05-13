package com.navinnayak.android.sleeptracker.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class SleepContract {

    public static final String CONTENT_AUTHORITY = "com.navinnayak.android.sleeptracker";
    public static final String PATH_SLEEP = "sleep";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    public static final class SleepEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_SLEEP);
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + PATH_SLEEP;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + PATH_SLEEP;

        public static final String TABLE_NAME = "sleep";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_SLEEP_START_TIME = "start_time";
        public static final String COLUMN_SLEEP_END_TIME = "end_time";
        public static final String COLUMN_LAST_UPDATED = "last_updated";
    }
}