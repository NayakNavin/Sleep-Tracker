package com.navinnayak.android.sleeptracker.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.navinnayak.android.sleeptracker.R;
import com.yinglan.circleviewlibrary.CircleAlarmTimerView;

import static com.navinnayak.android.sleeptracker.util.Util.getDate;

public class SetTargetActivity extends AppCompatActivity {


    public static final String SHARED_PREF = "sharedPrefs";
    long startTimeMilli;
    public static final String START_TIME = "startTime";
    private CircleAlarmTimerView circleAlarmTimerView;
    private TextView startTimeTV;
    private TextView endTimeTV;
    private TextView elapsedTimeTV;
    public static final String END_TIME = "endTime";
    private static final String TAG = SetTargetActivity.class.getCanonicalName();
    long endTimeMilli;
    Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_target);
        initialize();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveSharedPrefData();
                Intent intent = new Intent(SetTargetActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }


    public void saveSharedPrefData() {
        SharedPreferences prefs = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putLong(START_TIME, startTimeMilli);
        editor.putLong(END_TIME, endTimeMilli);
        editor.apply();

        Toast.makeText(this, "data saved", Toast.LENGTH_SHORT).show();
    }

    public void initialize() {
        startTimeTV = findViewById(R.id.bedTime);
        endTimeTV = findViewById(R.id.wakeUpTime);
        elapsedTimeTV = findViewById(R.id.elapsedTime);
        save = findViewById(R.id.done);

        circleAlarmTimerView = findViewById(R.id.circletimerview);
        circleAlarmTimerView.setOnTimeChangedListener(new CircleAlarmTimerView.OnTimeChangedListener() {
            @Override
            public void start(String startTime) {
                startTimeTV.setText(startTime);
                startTimeToMilli();
                elapsedTime();
            }

            @Override
            public void end(String endTime) {
                endTimeTV.setText(endTime);
                endTimeToMilli();
                elapsedTime();

                if (endTimeMilli < startTimeMilli) {
                    Log.d(TAG, "endtime < startTime");
                } else {
                    Log.d(TAG, "endtime > startTime");
                }
            }
        });
    }

    public void startTimeToMilli() {
        String startSubString1 = startTimeTV.getText().toString().substring(0, 2);
        String startSubString2 = startTimeTV.getText().toString().substring(3);
        long startHour = Long.parseLong(startSubString1);
        long startMinute = Long.parseLong(startSubString2);
        startTimeMilli = (startHour * 60 + startMinute) * 60 * 1000;
        Log.d(TAG, "start time: " + startTimeMilli);
    }

    public void endTimeToMilli() {
        String endSubString1 = endTimeTV.getText().toString().substring(0, 2);
        String endSubString2 = endTimeTV.getText().toString().substring(3);
        long endHour = Long.parseLong(endSubString1);
        long endMinute = Long.parseLong(endSubString2);
        endTimeMilli = (endHour * 60 + endMinute) * 60 * 1000;
        Log.d(TAG, "end time: " + endTimeMilli);
    }

    public void elapsedTime() {
        long balance = (endTimeMilli - startTimeMilli) - (330 * 60 * 1000);
        elapsedTimeTV.setText(getDate(balance, "HH 'hours and' mm 'minutes'"));
    }
}
