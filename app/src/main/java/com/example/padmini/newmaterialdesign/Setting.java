package com.example.padmini.newmaterialdesign;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;


public class Setting extends AppCompatActivity {

    Calendar calendar = Calendar.getInstance();

    Button setTimeButton;
    TextView displayTimeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setTimeButton = (Button) findViewById(R.id.setTimeButton);
        displayTimeText = (TextView) findViewById(R.id.displayTime);

        SharedPreferences preferences = getSharedPreferences("Passing_Hour_and_Minute_To_OptionMenu", Context.MODE_PRIVATE);
        final int user_seleted_alarm_hour = preferences.getInt("selectedHour", 0);
        final int user_selected_alarm_minute = preferences.getInt("selectedMinute", 0);
        displayTimeText.setText("Selected Time is:" +user_seleted_alarm_hour + ":" +user_selected_alarm_minute);

        setTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(Setting.this, onTimeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
            }
        });
    }


    TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

        public Calendar getAlamCal(int hour, int min) {

            int sec = 0;

            Calendar userSettingCal = Calendar.getInstance();
            userSettingCal.set(Calendar.HOUR_OF_DAY, hour);
            userSettingCal.set(Calendar.MINUTE, min);
            userSettingCal.set(Calendar.SECOND, sec);

            Calendar now = Calendar.getInstance();
            if (now.after(userSettingCal)) {
                userSettingCal.add(Calendar.DAY_OF_YEAR, 1); // add, not set!
            }

            return userSettingCal;
        }

        public void cancelAlarmIfExists(Context mContext, int requestCode, Intent intent) {
            try {
                PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, requestCode, intent, 0);
                AlarmManager am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
                am.cancel(pendingIntent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void setAlarm(Context context, Calendar cal) {
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, MyService.class);
            PendingIntent pi = PendingIntent.getService(context, 0, intent, 0);

            cancelAlarmIfExists(Setting.this, 0, intent);

            am.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 1000 * 60 * 60 * 24, pi); // Millisec * Second * Minute
        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            displayTimeText.setText("Selected Time is:" + hourOfDay + ":" + minute);
            SharedPreferences preferences = getSharedPreferences("Passing_Hour_and_Minute_To_OptionMenu", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            int selectedHour = hourOfDay;
            int selectedMinute = minute;
            editor.putInt("selectedHour", selectedHour);
            editor.putInt("selectedMinute", selectedMinute);
            editor.commit();

            Calendar alarmCal = getAlamCal(hourOfDay, minute);


            setAlarm(Setting.this, alarmCal);

        }
    };

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        SharedPreferences preferences = getSharedPreferences("Passing_Hour_and_Minute_To_OptionMenu", Context.MODE_PRIVATE);
        final int alarm_hour = preferences.getInt("selectedHour", 0);
        final int alarm_minute = preferences.getInt("selectedMinute", 0);
        setTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(Setting.this, onTimeSetListener,alarm_hour,alarm_minute, true).show();
            }
        });
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        onBackPressed();
        return true;


    }

}


