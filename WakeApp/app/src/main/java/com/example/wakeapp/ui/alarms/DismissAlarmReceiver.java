package com.example.wakeapp.ui.alarms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

// BroadcastReceiver to dismiss the alarm from the notification button

public class DismissAlarmReceiver extends BroadcastReceiver {

    public static String ACTION_DISMISS = "actionDismissAlarm";

    public DismissAlarmReceiver(){}

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(ACTION_DISMISS)){
            Intent intentService = new Intent(context, AlarmService.class);
            intentService.setAction("com.dismiss.action");
            context.stopService(intentService);
            context.sendBroadcast(intentService);

        }
    }
}
