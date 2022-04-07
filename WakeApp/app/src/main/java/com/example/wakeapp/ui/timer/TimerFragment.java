package com.example.wakeapp.ui.timer;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.SyncStateContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import com.example.wakeapp.MainActivity;
import com.example.wakeapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.jetbrains.annotations.NotNull;

public class TimerFragment extends Fragment {

    /** action buttons for starting pausing and resetting the timer */
    private FloatingActionButton mStartButton, mPauseButton, mStopButton;

    private LinearLayout timerLayoutInput, timerLayoutView;
    /** editText fields and Textviews to display and edit hours seconds and minutes*/
    private EditText hoursEditText, minutesEditText, secondsEditText;
    TextView hoursLeftText, minutesLeftText, secondsLeftText;

    /** int to keep track of remaining time*/
    int hoursLeft, minutesLeft, secondsLeft, totalSecondsLeft;

    int startTime;

    private CountDownTimer timer;

    /** boolean to keep track of the timer state */
    boolean isPaused = false;
    boolean isRunning = false;

    private final String timerChannelID = "timer_channel";


    @SuppressLint("DefaultLocale")
    private void updateRemainingTime(long msUntilFinished){
        totalSecondsLeft = (int) msUntilFinished / 1000;
        hoursLeft = totalSecondsLeft / 3600;
        minutesLeft = (totalSecondsLeft % 3600) / 60;
        secondsLeft = totalSecondsLeft % 60;

        hoursLeftText.setText(String.format("%02d", hoursLeft));
        minutesLeftText.setText(String.format("%02d", minutesLeft));
        secondsLeftText.setText(String.format("%02d", secondsLeft));
    }

    /** when timer is completed */
    public void finishTimer() {

        /* this should create a popup notification once the timer finishes
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_baseline_timer_24)
                .setContentTitle("Timer")
                .setContentText("time is up!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

         */

        /** set back visibility on input fields */
        timerLayoutInput.setVisibility(LinearLayout.VISIBLE);
        timerLayoutView.setVisibility(LinearLayout.INVISIBLE);
        secondsEditText.requestFocus();

        /** and reset to starting position all the buttons */
        mStartButton.setEnabled(true);
        mStartButton.setVisibility(FloatingActionButton.VISIBLE);
        mPauseButton.setEnabled(false);
        mPauseButton.setVisibility(FloatingActionButton.INVISIBLE);
        mStopButton.setEnabled(false);
        mStopButton.setVisibility(FloatingActionButton.INVISIBLE);

        /** TODO: IMPLEMENT POPUP NOTIFICATION */
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
        mStartButton = view.findViewById(R.id.start_button);
        mPauseButton = view.findViewById(R.id.pause_button);
        mStopButton = view.findViewById(R.id.stop_button);

        mStartButton.setEnabled(true);
        mStartButton.setVisibility(FloatingActionButton.VISIBLE);

        mPauseButton.setEnabled(false);
        mPauseButton.setVisibility(FloatingActionButton.INVISIBLE);
        mStopButton.setEnabled(false);
        mStopButton.setVisibility(FloatingActionButton.INVISIBLE);

        /** START BUTTON IMPLEMENTATION */
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //obscuring the input-field and enabling the timer view
                timerLayoutInput.setVisibility(LinearLayout.INVISIBLE);
                timerLayoutView.setVisibility(LinearLayout.VISIBLE);

                isRunning = true;
                //start the timer when start is clicked
                startTime = 0;

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
                }

                //setting up button visibility for the current state
                mStartButton.setEnabled(false);
                mStartButton.setVisibility(FloatingActionButton.INVISIBLE);

                mPauseButton.setEnabled(true);
                mPauseButton.setVisibility(FloatingActionButton.VISIBLE);
                mStopButton.setEnabled(true);
                mStopButton.setVisibility(FloatingActionButton.VISIBLE);

                //starting the timer from startTime with 1 second interval == 1000ms
                timer = new CountDownTimer(startTime, 1000) {
                    @Override
                    public void onTick(long msUntilFinished) {
                        updateRemainingTime(msUntilFinished);
                    }

                    @Override
                    public void onFinish() {
                        //TODO: alert notification
                        //finishTimer("Done");
                        finishTimer();
                    }
                }.start();
            }
        });

        /** PAUSE BUTTON IMPLEMENTATION */
        mPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //pause the timer when this is clicked
                isPaused = !isPaused; //switch the paused state
                if (isPaused){

                    timerLayoutInput.setVisibility(LinearLayout.INVISIBLE);
                    timerLayoutView.setVisibility(LinearLayout.VISIBLE);

                    mStartButton.setEnabled(true);
                    mStartButton.setVisibility(FloatingActionButton.VISIBLE);
                    mPauseButton.setEnabled(false);
                    mPauseButton.setVisibility(FloatingActionButton.INVISIBLE);
                    mStopButton.setEnabled(false);
                    mStopButton.setVisibility(FloatingActionButton.INVISIBLE);

                    timer.cancel();
                }
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
                secondsEditText.requestFocus();

                //resetting the textview
                hoursLeftText.setText("00");
                minutesLeftText.setText("00");
                secondsLeftText.setText("00");

                //finishTimer("Cancelled");
                finishTimer();
            }
        });
    }

    /** whenever the fragment is called by the main activity */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_timer, container, false);

        //calling helper function to setup text and timer buttons
        setupEditText(view);
        setupTimerButtons(view);
        return view;
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