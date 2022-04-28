package com.example.wakeapp.ui.alarms;

import android.annotation.SuppressLint;
import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.*;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import com.example.wakeapp.R;
import com.example.wakeapp.App;
import static android.os.VibrationEffect.DEFAULT_AMPLITUDE;
//import static com.example.wakeapp.App.CHANNEL_ID;
import static com.example.wakeapp.ui.alarms.AlarmBroadcastReceiver.TITLE;
import static com.example.wakeapp.ui.alarms.DismissAlarmReceiver.ACTION_DISMISS;
import static com.example.wakeapp.ui.alarms.SnoozeAlarmReceiver.ACTION_SNOOZE;

//The BroadcastReceiver will start a Service that will be used for the alarm. This Service will display a
// notification and will play an audio track for the alarm sound on loop and produce a vibration effect until
// the alarm is dismissed or snoozed.

public class AlarmService extends Service {
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;
    private VibratorManager vibratorManager;
    private final String alarmChannelID = "alarm_channel";

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    public void onCreate() {
        super.onCreate();

        mediaPlayer = MediaPlayer.create(this, R.raw.alarm);
        mediaPlayer.setLooping(true);

        vibratorManager = (VibratorManager) getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
        vibrator = vibratorManager.getDefaultVibrator();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent notificationIntent = new Intent(this, RingActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        // Notification Button for Snooze
        Intent snoozeIntent = new Intent(this,SnoozeAlarmReceiver.class);
        snoozeIntent.setAction(ACTION_SNOOZE);
        PendingIntent snoozePendingIntent = PendingIntent.getBroadcast(this,0,snoozeIntent,
                PendingIntent.FLAG_IMMUTABLE);

        // Notification Button for Dismiss
        Intent dismissIntent = new Intent(this,DismissAlarmReceiver.class);
        dismissIntent.setAction(ACTION_DISMISS);
        PendingIntent dismissPendingIntent = PendingIntent.getBroadcast(this,0,dismissIntent,
                PendingIntent.FLAG_IMMUTABLE);

        String alarmTitle = String.format("%s Alarm", intent.getStringExtra(TITLE));

        // Builder of the Notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,"channelID")
                .setSmallIcon(R.drawable.alarm_icon)
                .setContentTitle(alarmTitle)
                .setContentText("Ring Ring ... Ring Ring")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setFullScreenIntent(pendingIntent,true)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .addAction(R.drawable.ic_baseline_delete_24,"Snooze",snoozePendingIntent)
                .addAction(R.drawable.ic_baseline_alarm_off_24,"Dismiss",dismissPendingIntent);


        mediaPlayer.start();
        long[] pattern = { 0, 100, 1000 };
        vibrator.vibrate(VibrationEffect.createWaveform(pattern,0));

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int notificationId = 1;
        createChannel(notificationManager);
        notificationManager.notify(notificationId, notificationBuilder.build());
        startForeground(1,notificationBuilder.build());

        return START_STICKY;
    }


    // To create the notification Channel
    public void createChannel(NotificationManager notificationManager){
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }
        NotificationChannel channel = new NotificationChannel(alarmChannelID,alarmChannelID,
                NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("channel_description");
        notificationManager.createNotificationChannel(channel);
    }



    @Override
    public void onDestroy() {
        super.onDestroy();

        mediaPlayer.stop();
        vibrator.cancel();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
