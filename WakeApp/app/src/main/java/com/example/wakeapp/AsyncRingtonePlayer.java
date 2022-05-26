package com.example.wakeapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.telephony.TelephonyManager;

import java.io.IOException;
import java.lang.reflect.Method;

import static android.media.AudioManager.AUDIOFOCUS_GAIN_TRANSIENT;
import static android.media.AudioManager.STREAM_ALARM;


public final class AsyncRingtonePlayer {

    // Volume suggested by media team for in-call alarms.
    private static final float IN_CALL_VOLUME = 0.125f;

    // Message codes used with the ringtone thread.
    private static final int EVENT_PLAY = 1;
    private static final int EVENT_STOP = 2;
    private static final int EVENT_VOLUME = 3;
    private static final String RINGTONE_URI_KEY = "RINGTONE_URI_KEY";
    private static final String CRESCENDO_DURATION_KEY = "CRESCENDO_DURATION_KEY";

    /** Handler running on the ringtone thread. */
    private Handler mHandler;

    private PlaybackDelegate mPlaybackDelegate;

    /** The context. */
    private final Context mContext;

    public AsyncRingtonePlayer(Context context) {
        mContext = context;
    }

    /** Plays the ringtone. */
    public void play(Uri ringtoneUri, long crescendoDuration) {
        postMessage(EVENT_PLAY, ringtoneUri, crescendoDuration, 0);
    }

    /** Stops playing the ringtone. */
    public void stop() {
        postMessage(EVENT_STOP, null, 0, 0);
    }

    /** Schedules an adjustment of the playback volume 50ms in the future. */
    private void scheduleVolumeAdjustment() {
        // Ensure we never have more than one volume adjustment queued.
        mHandler.removeMessages(EVENT_VOLUME);

        // Queue the next volume adjustment.
        postMessage(EVENT_VOLUME, null, 0, 50);
    }



    /**
     * Posts a message to the ringtone-thread handler.
     *
     * @param messageCode the message to post
     * @param ringtoneUri the ringtone in question, if any
     * @param crescendoDuration the length of time, in ms, over which to crescendo the ringtone
     * @param delayMillis the amount of time to delay sending the message, if any
     */
    private void postMessage(int messageCode, Uri ringtoneUri, long crescendoDuration,
                             long delayMillis) {
        synchronized (this) {

            final Message message = mHandler.obtainMessage(messageCode);
            if (ringtoneUri != null) {
                final Bundle bundle = new Bundle();
                bundle.putParcelable(RINGTONE_URI_KEY, ringtoneUri);
                bundle.putLong(CRESCENDO_DURATION_KEY, crescendoDuration);
                message.setData(bundle);
            }

            mHandler.sendMessageDelayed(message, delayMillis);
        }
    }


    /**
     * @return <code>true</code> iff the device is currently in a telephone call
     */
    private static boolean isInTelephoneCall(Context context) {
        final TelephonyManager tm = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getCallState() != TelephonyManager.CALL_STATE_IDLE;
    }


    /**
     * @param currentTime current time of the device
     * @param stopTime time at which the crescendo finishes
     * @param duration length of time over which the crescendo occurs
     * @return the scalar volume value that produces a linear increase in volume (in decibels)
     */
    private static float computeVolume(long currentTime, long stopTime, long duration) {
        // Compute the percentage of the crescendo that has completed.
        final float elapsedCrescendoTime = stopTime - currentTime;
        final float fractionComplete = 1 - (elapsedCrescendoTime / duration);

        // Use the fraction to compute a target decibel between -40dB (near silent) and 0dB (max).
        final float gain = (fractionComplete * 40) - 40;

        // Convert the target gain (in decibels) into the corresponding volume scalar.
        final float volume = (float) Math.pow(10f, gain/20f);

        return volume;
    }

    /**
     * This interface abstracts away the differences between playing ringtones via {@link Ringtone}
     * vs {@link MediaPlayer}.
     */
    private interface PlaybackDelegate {
        /**
         * @return {@code true} iff a {@link #adjustVolume volume adjustment} should be scheduled
         */
        boolean play(Context context, Uri ringtoneUri, long crescendoDuration);

        /**
         * Stop any ongoing ringtone playback.
         */
        void stop(Context context);

        /**
         * @return {@code true} iff another volume adjustment should be scheduled
         */
        boolean adjustVolume(Context context);
    }

    /**
     * Loops playback of a ringtone using {@link MediaPlayer}.
     */
    private class MediaPlayerPlaybackDelegate implements PlaybackDelegate {

        /** The audio focus manager. Only used by the ringtone thread. */
        private AudioManager mAudioManager;

        /** Non-{@code null} while playing a ringtone; {@code null} otherwise. */
        private MediaPlayer mMediaPlayer;

        /** The duration over which to increase the volume. */
        private long mCrescendoDuration = 0;

        /** The time at which the crescendo shall cease; 0 if no crescendo is present. */
        private long mCrescendoStopTime = 0;

        /**
         * Starts the actual playback of the ringtone. Executes on ringtone-thread.
         */
        @Override
        public boolean play(final Context context, Uri ringtoneUri, long crescendoDuration) {
            mCrescendoDuration = crescendoDuration;

            if (mAudioManager == null) {
                mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            }

            final boolean inTelephoneCall = isInTelephoneCall(context);
            Uri alarmNoise = ringtoneUri;
            // Fall back to the system default alarm if the database does not have an alarm stored.
            if (alarmNoise == null) {
                alarmNoise = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            }

            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    stop(context);
                    return true;
                }
            });

            try {
                // If alarmNoise is a custom ringtone on the sd card the app must be granted
                // android.permission.READ_EXTERNAL_STORAGE. Pre-M this is ensured at app
                // installation time. M+, this permission can be revoked by the user any time.
                mMediaPlayer.setDataSource(context, alarmNoise);

                return startPlayback(inTelephoneCall);
            } catch (Throwable t) {
                // The alarmNoise may be on the sd card which could be busy right now.
                // Use the fallback ringtone.
                try {
                    // Must reset the media player to clear the error state.
                    mMediaPlayer.reset();
                    return startPlayback(inTelephoneCall);
                } catch (Throwable t2) {
                    // At this point we just don't play anything.
                }
            }

            return false;
        }

        /**
         * Prepare the MediaPlayer for playback if the alarm stream is not muted, then start the
         * playback.
         *
         * @param inTelephoneCall {@code true} if there is currently an active telephone call
         * @return {@code true} if a crescendo has started and future volume adjustments are
         *      required to advance the crescendo effect
         */
        private boolean startPlayback(boolean inTelephoneCall)
                throws IOException {
            // Do not play alarms if stream volume is 0 (typically because ringer mode is silent).
            if (mAudioManager.getStreamVolume(STREAM_ALARM) == 0) {
                return false;
            }

            // Check if we are in a call. If we are, use the in-call alarm resource at a low volume
            // to not disrupt the call.
            boolean scheduleVolumeAdjustment = false;
            if (inTelephoneCall) {
                mMediaPlayer.setVolume(IN_CALL_VOLUME, IN_CALL_VOLUME);
            } else if (mCrescendoDuration > 0) {
                mMediaPlayer.setVolume(0, 0);
                scheduleVolumeAdjustment = true;
            }

            mMediaPlayer.setAudioStreamType(STREAM_ALARM);
            mMediaPlayer.setLooping(true);
            mMediaPlayer.prepare();
            mAudioManager.requestAudioFocus(null, STREAM_ALARM, AUDIOFOCUS_GAIN_TRANSIENT);
            mMediaPlayer.start();

            return scheduleVolumeAdjustment;
        }

        /**
         * Stops the playback of the ringtone. Executes on the ringtone-thread.
         */
        @Override
        public void stop(Context context) {

            mCrescendoDuration = 0;
            mCrescendoStopTime = 0;

            // Stop audio playing
            if (mMediaPlayer != null) {
                mMediaPlayer.stop();
                mMediaPlayer.release();
                mMediaPlayer = null;
            }

            if (mAudioManager != null) {
                mAudioManager.abandonAudioFocus(null);
            }
        }

        /**
         * Adjusts the volume of the ringtone being played to create a crescendo effect.
         */
        @Override
        public boolean adjustVolume(Context context) {

            // If media player is absent or not playing, ignore volume adjustment.
            if (mMediaPlayer == null || !mMediaPlayer.isPlaying()) {
                mCrescendoDuration = 0;
                mCrescendoStopTime = 0;
                return false;
            }

            return true;
        }
    }
}