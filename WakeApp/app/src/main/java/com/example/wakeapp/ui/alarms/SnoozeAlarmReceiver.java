package com.example.wakeapp.ui.alarms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;
import java.util.Random;

// BroadcastReceiver to snooze the alarm from the notification button

public class SnoozeAlarmReceiver extends BroadcastReceiver {

    public static String ACTION_SNOOZE = "actionSnoozeAlarm";

    public SnoozeAlarmReceiver(){}

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(ACTION_SNOOZE)){

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.add(Calendar.MINUTE, 10);

            Alarm alarm = new Alarm(
                    new Random().nextInt(Integer.MAX_VALUE),
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    "Snooze",
                    System.currentTimeMillis(),
                    true,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false
            );

            alarm.schedule(context);

            Intent intentService = new Intent(context, AlarmService.class);
            intentService.setAction("com.snooze.action");
            context.stopService(intentService);
            context.sendBroadcast(intentService);

        }
    }
}
