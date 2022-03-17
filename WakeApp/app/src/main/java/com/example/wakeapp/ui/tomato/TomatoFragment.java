package com.example.wakeapp.ui.tomato;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.wakeapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class TomatoFragment extends Fragment implements Button.OnClickListener{

    private FloatingActionButton mStartButton, mPauseButton, getmCancelButton;
    private EditText hoursEditText, minutesEditText, secondsEditText;

    int hoursLeft, minutesLeft, secondsLeft, totalSecondsLeft;
    TextView hoursLeftText, minutesLeftText, secondsLeftText;
    TextView countDownText;

    private CountDownTimer timer;

    boolean isPaused = false;

    /*
    *
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        tomatoViewModel =
                new ViewModelProvider(this).get(TomatoViewModel.class);

        binding = FragmentTomatoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }
    */

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_tomato, container, false);
        hoursEditText = view.findViewById(R.id.hours);
        minutesEditText = view.findViewById(R.id.minutes);
        secondsEditText = view.findViewById(R.id.seconds);


        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    //play timer click listener
    @Override
    public void onClick(View view){



    }
}
