package com.example.wakeapp.ui.timer;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.wakeapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.jetbrains.annotations.NotNull;

public class TimerFragment extends Fragment {

    private FloatingActionButton mStartButton, mPauseButton, mStopButton;
    private EditText hoursEditText, minutesEditText, secondsEditText;

    int hoursLeft, minutesLeft, secondsLeft, totalSecondsLeft;
    TextView hoursLeftText, minutesLeftText, secondsLeftText;
    //TextView countDownText;

    int startTime;

    private CountDownTimer timer;

    boolean isPaused = false;

    @SuppressLint("DefaultLocale")
    private void updateRemainingTime(long msUntilFinished){
        totalSecondsLeft = (int) msUntilFinished / 1000;
        hoursLeft = totalSecondsLeft / 3600;
        minutesLeft = (totalSecondsLeft % 3600) / 6;
        secondsLeft = totalSecondsLeft % 60;

        hoursLeftText.setText(String.format("%02d", hoursLeft));
        minutesLeftText.setText(String.format("%02d", minutesLeft));
        secondsLeftText.setText(String.format("%02d", secondsLeft));

    }

    public void finishTimer() {
        //countDownText.setText(message);
        mStartButton.setEnabled(true);
        mPauseButton.setEnabled(false);
        mStopButton.setEnabled(false);
        //mRestartButton.setEnabled(false);

    }


    //helper function to setup edit input texts
    public void setupEditText(View view){
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
    //helper function to setup button functionality
    public void setupTimerButtons(@NotNull View view) {
        mStartButton = view.findViewById(R.id.start_button);
        mPauseButton = view.findViewById(R.id.pause_button);
        mStopButton = view.findViewById(R.id.stop_button);

        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //start the timer when start is clicked
                startTime = totalSecondsLeft;

                //multiply because we convert everything to milliseconds
                startTime += Integer.parseInt(secondsEditText.getText().toString())*1000;
                startTime += Integer.parseInt(minutesEditText.getText().toString())*1000 * 60;
                startTime += Integer.parseInt(hoursEditText.getText().toString())*1000 * 60 * 60;


                //setting up button visibility for the current state
                mStartButton.setEnabled(false);
                mPauseButton.setEnabled(true);
                mStopButton.setEnabled(true);
                //mRestartButton.setEnabled(true); //TODO: implement restart button (?)

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

        mPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //pause the timer when this is clicked
                isPaused = !isPaused; //switch the paused state
                if (isPaused){
                    mStartButton.setEnabled(true);
                    mPauseButton.setEnabled(false);
                    timer.cancel();
                    //countDownText.setText("Paused");
                } else {
                    timer = new CountDownTimer((long) totalSecondsLeft * 1000, 1000) {
                        @Override
                        public void onTick(long msUntilFinished) {
                            updateRemainingTime(msUntilFinished);
                        }

                        @Override
                        public void onFinish() {
                            //finishTimer("Done");
                            finishTimer();
                        }
                    }.start();
                }
            }
        });

        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //stop/cancel the timer when this is clicked
                timer.cancel();
                //finishTimer("Cancelled");
                finishTimer();
            }
        });
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_timer, container, false);

        //calling helper function to setup text and timer buttons
        setupEditText(view);
        setupTimerButtons(view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}
