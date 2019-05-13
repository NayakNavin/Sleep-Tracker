package com.navinnayak.android.sleeptracker;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.navinnayak.android.sleeptracker.data.SleepContract.SleepEntry;

import static com.navinnayak.android.sleeptracker.Util.getDate;

public class SleepCursorAdapter extends CursorAdapter {

    public SleepCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(final View view, final Context context, final Cursor cursor) {

        TextView startTextView = view.findViewById(R.id.start_time);
        TextView endTextView = view.findViewById(R.id.end_time);


        int startColumnIndex = cursor.getColumnIndex(SleepEntry.COLUMN_SLEEP_START_TIME);
        long startDate = cursor.getLong(startColumnIndex);

        startTextView.setText(getDate(startDate, "HH:mm:ss MMM dd EEE, yyyy "));


        int endColumnIndex = cursor.getColumnIndex(SleepEntry.COLUMN_SLEEP_END_TIME);
        long endDate = cursor.getLong(endColumnIndex);

        endTextView.setText(getDate(endDate, "HH:mm:ss MMM dd EEE, yyyy "));
    }
}