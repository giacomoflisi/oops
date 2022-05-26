package com.example.wakeapp.ui.timer;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import com.example.wakeapp.AsyncRingtonePlayer;

import static android.os.VibrationEffect.DEFAULT_AMPLITUDE;

public abstract class TimerRingtone {

    private static final long[] VIBRATE_PATTERN = {500, 500};

    private static boolean sStarted = false;
    private static AsyncRingtonePlayer sAsyncRingtonePlayer;

    private static final long[] timings = {0, DEFAULT_AMPLITUDE};
    private static VibrationEffect vibrationEffect;
    private TimerRingtone() {
    }

    public static void stop(Context context) {
        if (sStarted) {
            sStarted = false;
            getAsyncRingtonePlayer(context).stop();
            ((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE)).cancel();
        }
    }

    public static void start(Context context) {
        // Make sure we are stopped before starting
        stop(context);

        // Look up user-selected timer ringtone.
        final Vibrator vibrator = getVibrator(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrator.vibrate(VibrationEffect.createWaveform(timings, 500));
        }
        sStarted = true;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static void vibrateLOrLater(Vibrator vibrator) {
        vibrator.vibrate(VIBRATE_PATTERN, 0, new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build());
    }

    private static Vibrator getVibrator(Context context) {
        return ((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE));
    }

    private static synchronized AsyncRingtonePlayer getAsyncRingtonePlayer(Context context) {
        if (sAsyncRingtonePlayer == null) {
            sAsyncRingtonePlayer = new AsyncRingtonePlayer(context.getApplicationContext());
        }

        return sAsyncRingtonePlayer;
    }
}