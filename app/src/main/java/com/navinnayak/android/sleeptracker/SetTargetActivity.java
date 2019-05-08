package com.navinnayak.android.sleeptracker;

import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.yinglan.circleviewlibrary.CircleAlarmTimerView;

import java.text.SimpleDateFormat;

public class SetTargetActivity extends AppCompatActivity {


    private static final String TAG = SetTargetActivity.class.getCanonicalName();
    long startTimeMilli;
    long endTimeMilli;
    private CircleAlarmTimerView circleAlarmTimerView;
    private TextView startTimeTV;
    private TextView endTimeTV;
    private TextView elapsedTimeTV;

    Button save;

    public static final String SHARED_PREF = "sharedPrefs";
    public static final String START_TEXT = "startTime";
    public static final String END_TEXT = "endTime";
    public static final String ELAPSED_TEXT = "elapsedTime";

    private String startPrefText;
    private String endPrefText;
    private String elapsedPrefText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_target);
        init();

//        // To remove the shadow
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.setElevation(0);
//        }
//        actionBar.setDisplayHomeAsUpEnabled(false);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                saveSharedPrefData();
            }
        });
//        loadSharedPrefData();
//        updateSharedPrefData();
    }

//    public void saveSharedPrefData() {
//        SharedPreferences prefs = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
//        SharedPreferences.Editor editor = prefs.edit();
//
////        editor.putString(START_TEXT, startTimeTV.getText().toString());
////        Log.d(TAG, "saved start: " + START_TEXT.getBytes());
//
//        editor.putString(END_TEXT, endTimeTV.getText().toString());
//        Log.d(TAG, "saved end: " + END_TEXT.getBytes());
//
//        editor.putString(ELAPSED_TEXT, elapsedTimeTV.getText().toString());
//        Log.d(TAG, "saved elapsed: " + ELAPSED_TEXT.getBytes());
//
//
//        editor.apply();
//
//
//        Toast.makeText(this, "data saved", Toast.LENGTH_SHORT).show();
//
//    }

//    public void loadSharedPrefData() {
//
//
//        SharedPreferences prefs = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
//
////        startPrefText = prefs.getString(START_TEXT, "");
////        Log.d(TAG, "load start: " + startPrefText);
//
//        endPrefText = prefs.getString(END_TEXT, "");
//        Log.d(TAG, "load end: " + endPrefText);
//
//        elapsedPrefText = prefs.getString(ELAPSED_TEXT, "");
//        Log.d(TAG, "load elapsed: " + elapsedPrefText);
//
////        updateSharedPrefData();
//
//    }
//
//    public void updateSharedPrefData() {
//
//
////        startTimeTV.setText(startPrefText);
////        Log.d(TAG, "update start: " + startTimeTV.getText());
//
//        endTimeTV.setText(endPrefText);
//        Log.d(TAG, "update end: " + endTimeTV.getText());
//
//        elapsedTimeTV.setText(elapsedPrefText);
//        Log.d(TAG, "update elapsed: " + elapsedTimeTV.getText());
//
//    }



    public void init() {
        startTimeTV = findViewById(R.id.bedTime);
        endTimeTV = findViewById(R.id.wakeUpTime);
        elapsedTimeTV = findViewById(R.id.elapsedTime);
        save = findViewById(R.id.done);


        circleAlarmTimerView = findViewById(R.id.circletimerview);
        circleAlarmTimerView.setOnTimeChangedListener(new CircleAlarmTimerView.OnTimeChangedListener() {
            @Override
            public void start(String starting) {
                startTimeTV.setText(starting);

                startTimeToMilli();
                elapsedTime();

            }

            @Override
            public void end(String ending) {
                endTimeTV.setText(ending);
                endTimeToMilli();
                elapsedTime();
            }
        });
    }


    public void startTimeToMilli() {
        String startSubString1 = startTimeTV.getText().toString().substring(0, 2);
        String startSubString2 = startTimeTV.getText().toString().substring(3);

        long startHour = Long.parseLong(startSubString1);
        long startMinute = Long.parseLong(startSubString2);
        startTimeMilli = (startHour * 3600000) + (startMinute * 60000);


    }


    public void endTimeToMilli() {
        String endSubString1 = endTimeTV.getText().toString().substring(0, 2);
        String endSubString2 = endTimeTV.getText().toString().substring(3);

        long endHour = Long.parseLong(endSubString1);
        long endMinute = Long.parseLong(endSubString2);
        endTimeMilli = (endHour * 3600000) + (endMinute * 60000);

    }


    public static String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }


    public void elapsedTime() {
        long bal = (endTimeMilli - startTimeMilli) - (330 * 60 * 1000);
        elapsedTimeTV.setText(getDate(bal, "HH 'hours and' mm 'minutes'"));
    }

}
