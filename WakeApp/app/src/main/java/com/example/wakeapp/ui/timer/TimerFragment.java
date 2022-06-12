package com.example.wakeapp.ui.timer;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import com.example.wakeapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static android.app.Notification.EXTRA_NOTIFICATION_ID;
import static com.example.wakeapp.ui.alarms.SnoozeAlarmReceiver.ACTION_SNOOZE;

public class TimerFragment extends Fragment {

    // TODO: create intent to open the timer fragment from the notification
    // TODO: add a pause and stop button to the notification

    /* TODO: save fragments state when switching between them,
        so that they won't reset when we go back to them */

    Uri notification;
    View view;
    Ringtone ringtone;
    /** action buttons for starting pausing and resetting the timer */
    private FloatingActionButton mStartButton, mPauseButton, mStopButton;
    private Button mClearButton; //clearing input textview

    private LinearLayout timerLayoutInput, timerLayoutView;
    /** editText fields and Textviews to display and edit hours seconds and minutes*/
    private EditText hoursEditText, minutesEditText, secondsEditText;
    TextView hoursLeftText, minutesLeftText, secondsLeftText;

    /** int to keep track of remaining time*/
    int hoursLeft, minutesLeft, secondsLeft, totalSecondsLeft;
    float progress;

    int startTime, totalTime;

    private CountDownTimer timer;

    CircularProgressBar progressBar;

    /** boolean to keep track of the timer state */
    boolean isPaused;
    boolean isStop;
    boolean isRunning;

    private final String timerChannelID = "timer_channel";
    NotificationChannel channel;
    NotificationManagerCompat notificationManager;
    NotificationCompat.Builder builder;
    int importance = NotificationManager.IMPORTANCE_DEFAULT;
    NotificationCompat.Builder builderFinish;

    private Context mContext;

    PendingIntent pendingIntent;
    Intent intent;

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mContext = null;
    }

    // Create an explicit intent
    public void setupIntent(){
        intent = new Intent(mContext, TimerFragment.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        pendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        Intent snoozeIntent = new Intent(mContext, TimerFragment.class);
        snoozeIntent.setAction(ACTION_SNOOZE);
        snoozeIntent.putExtra(EXTRA_NOTIFICATION_ID, 0);
        PendingIntent snoozePendingIntent =
                PendingIntent.getBroadcast(mContext, 0, snoozeIntent, 0);
    }

    private void setupRingtone(){
        notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        ringtone = RingtoneManager.getRingtone(mContext, notification);
    }

    @SuppressLint("DefaultLocale")
    private void updateRemainingTime(long msUntilFinished){

        totalSecondsLeft = (int) msUntilFinished / 1000;
        hoursLeft = totalSecondsLeft / 3600;
        minutesLeft = (totalSecondsLeft % 3600) / 60;
        secondsLeft = totalSecondsLeft % 60;

        hoursLeftText.setText(String.format("%02d", hoursLeft));
        minutesLeftText.setText(String.format("%02d", minutesLeft));
        secondsLeftText.setText(String.format("%02d", secondsLeft));

        progress = (float) (msUntilFinished - 1000) / (float) totalTime * 100;
        // casting to int to round down the percentage
        progress = (int) progress;
        progressBar.setProgressWithAnimation(progress);
        //setting notification progress bar
        builder.setProgress(100, (int) progress, false);
        builder.setContentText(totalSecondsLeft+" s left");
        notificationManager.notify(1, builder.build());
    }

    @SuppressLint("ResourceAsColor")
    private void createProgressNotification() {
        notificationManager = NotificationManagerCompat.from(mContext);

        builder = new NotificationCompat.Builder(
                Objects.requireNonNull(getActivity()).getApplicationContext(),
                timerChannelID);

        builder.setSmallIcon(R.drawable.timer_icon)
                .setContentTitle("Timer")
                .setContentText("Running")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setColor(getResources().getColor(R.color.purple_500))
                .setAutoCancel(true);
        builder.setProgress(100, 0, false);
        notificationManager.notify(1, builder.build());
    }

    private void createNotificationChannel(){
        channel = new NotificationChannel(timerChannelID, timerChannelID, importance);
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        NotificationManager notificationManager =
                (NotificationManager) Objects.requireNonNull(getActivity()).getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);

        // this should create a popup notification once the timer has finished
        builderFinish = new NotificationCompat.Builder(
                Objects.requireNonNull(getActivity()).getApplicationContext(),
                timerChannelID);
    }

    /** when timer is completed */
    public void finishTimer() {

        builderFinish.setSmallIcon(R.drawable.timer_icon)
                .setContentTitle("Timer")
                .setContentText("Time's up!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                //.addAction(R.drawable.ic_baseline_snooze_24, "stop", snoozePendingIntent)
                .setProgress(0, 0, false)
                .setColor(getResources().getColor(R.color.purple_500))
                .setAutoCancel(true);

        notificationManager.notify(1, builderFinish.build());
        ringtone.play();

        /* progress bar completed returns to zer0 percent */
        progressBar.setProgressWithAnimation(0);
        //progressBar.setVisibility(View.INVISIBLE);

        /* set back visibility on input fields */
        timerLayoutInput.setVisibility(LinearLayout.VISIBLE);
        timerLayoutView.setVisibility(LinearLayout.INVISIBLE);

        /* and reset to starting position all the buttons */
        mClearButton.setEnabled(true);
        mClearButton.setVisibility(Button.VISIBLE);
        mStartButton.setEnabled(true);
        mStartButton.setVisibility(FloatingActionButton.VISIBLE);

        mPauseButton.setEnabled(false);
        mPauseButton.setVisibility(FloatingActionButton.INVISIBLE);
        mStopButton.setEnabled(false);
        mStopButton.setVisibility(FloatingActionButton.INVISIBLE);
    }


    /** helper function to setup edit input texts */
    public void setupEditText(View view){
        //setting up edit whole layout and textview layout to be able to manage their visibility
        timerLayoutInput = view.findViewById(R.id.timer_text_input);
        timerLayoutView = view.findViewById(R.id.timer_text_view);
        //obscuring the view because we want to focus on the input text
        timerLayoutView.setVisibility(LinearLayout.INVISIBLE);

        hoursEditText = view.findViewById(R.id.hours);
        minutesEditText = view.findViewById(R.id.minutes);
        secondsEditText = view.findViewById(R.id.seconds);

        hoursLeftText = view.findViewById(R.id.hoursLeftText);
        minutesLeftText = view.findViewById(R.id.minutesLeftText);
        secondsLeftText = view.findViewById(R.id.secondsLeftText);

        hoursEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            /** change edittext focus if we reach lenght 2 */
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 2){
                    minutesEditText.requestFocus();
                }
            }
        });

        minutesEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 2){
                    secondsEditText.requestFocus();
                }
            }
        });

    }

    /** helper function to setup button functionality */
    public void setupTimerButtons(@NotNull View view) {

        mClearButton = view.findViewById(R.id.clear_button);
        mStartButton = view.findViewById(R.id.start_button);
        mPauseButton = view.findViewById(R.id.pause_button);
        mStopButton = view.findViewById(R.id.stop_button);

        mClearButton.setEnabled(true);
        mClearButton.setVisibility(Button.VISIBLE);
        mStartButton.setEnabled(true);
        mStartButton.setVisibility(FloatingActionButton.VISIBLE);

        mPauseButton.setEnabled(false);
        mPauseButton.setVisibility(FloatingActionButton.INVISIBLE);
        mStopButton.setEnabled(false);
        mStopButton.setVisibility(FloatingActionButton.INVISIBLE);

        // CLEAR INPUT TEXT BUTTON
        mClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                totalSecondsLeft = 0;
                hoursEditText.setText("00");
                minutesEditText.setText("00");
                secondsEditText.setText("00");
            }
        });


        /* START BUTTON IMPLEMENTATION */
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                createProgressNotification();
                //obscuring the input-field and enabling the timer view
                timerLayoutInput.setVisibility(LinearLayout.INVISIBLE);
                timerLayoutView.setVisibility(LinearLayout.VISIBLE);

                //start the timer when start is clicked
                startTime = 0;

                if (secondsEditText.getText().toString().isEmpty()){
                    secondsEditText.setText("00");
                }
                if (minutesEditText.getText().toString().isEmpty()){
                    minutesEditText.setText("00");
                }
                if (hoursEditText.getText().toString().isEmpty()){
                    hoursEditText.setText("00");
                }

                //multiply because we convert everything to milliseconds
                if (isPaused){
                    timer.cancel();
                    startTime += Integer.parseInt(secondsLeftText.getText().toString())*1000;
                    startTime += Integer.parseInt(minutesLeftText.getText().toString())*1000 * 60;
                    startTime += Integer.parseInt(hoursLeftText.getText().toString())*1000 * 60 * 60;
                } else {
                    startTime += Integer.parseInt(secondsEditText.getText().toString()) * 1000;
                    startTime += Integer.parseInt(minutesEditText.getText().toString()) * 1000 * 60;
                    startTime += Integer.parseInt(hoursEditText.getText().toString()) * 1000 * 60 * 60;
                    totalTime = startTime;
                }

                //setting up button visibility for the current state
                mStartButton.setEnabled(false);
                mStartButton.setVisibility(FloatingActionButton.INVISIBLE);

                mPauseButton.setEnabled(true);
                mPauseButton.setVisibility(FloatingActionButton.VISIBLE);
                mStopButton.setEnabled(true);
                mStopButton.setVisibility(FloatingActionButton.VISIBLE);

                mClearButton.setEnabled(false);
                mClearButton.setVisibility(Button.INVISIBLE);

                isPaused = false;
                isRunning = true;
                //starting the timer from startTime with 1 second interval == 1000ms
                timer = new CountDownTimer(startTime, 1000) {
                    @Override
                    public void onTick(long msUntilFinished) {
                        updateRemainingTime(msUntilFinished);
                    }

                    @Override
                    public void onFinish() {
                        finishTimer();
                    }
                }.start();
            }
        });

        /* PAUSE BUTTON IMPLEMENTATION */
        mPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //pause the timer when this is clicked
                isPaused = true; //switch the paused state
                isRunning = false;

                timerLayoutInput.setVisibility(LinearLayout.INVISIBLE);
                timerLayoutView.setVisibility(LinearLayout.VISIBLE);

                mStartButton.setEnabled(true);
                mStartButton.setVisibility(FloatingActionButton.VISIBLE);
                mPauseButton.setEnabled(false);
                mPauseButton.setVisibility(FloatingActionButton.INVISIBLE);
                mStopButton.setEnabled(false);
                mStopButton.setVisibility(FloatingActionButton.INVISIBLE);

                //cancel the countdown
                timer.cancel();
            }
        });

        /** STOP aka RESET BUTTON IMPLEMENTATION */
        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //stop/cancel the timer when this is clicked
                timer.cancel();

                timerLayoutInput.setVisibility(LinearLayout.VISIBLE);
                timerLayoutView.setVisibility(LinearLayout.INVISIBLE);

                //resetting the textview
                hoursLeftText.setText("00");
                minutesLeftText.setText("00");
                secondsLeftText.setText("00");

                isPaused = false;
                isStop = true;
                isRunning = false;

                finishTimer();
            }
        });
    }

    /** whenever the fragment is called by the main activity */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        //if (view == null) {
        view = inflater.inflate(R.layout.fragment_timer, container, false);
        //helper functions to setup text, buttons, notifications
        progressBar = view.findViewById(R.id.circular_progress_bar);
        progressBar.setStrokeWidth(50f);
        setupEditText(view);
        setupTimerButtons(view);
        createNotificationChannel();
        setupIntent();
        setupRingtone();

        isPaused = false;
        isRunning = false;
        isStop = true;
        //}

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}