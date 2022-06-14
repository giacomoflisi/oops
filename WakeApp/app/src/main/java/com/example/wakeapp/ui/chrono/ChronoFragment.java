package com.example.wakeapp.ui.chrono;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.wakeapp.R;
import com.example.wakeapp.databinding.FragmentChronoBinding;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ChronoFragment extends Fragment {

    private ChronoViewModel clockViewModel;
    private FragmentChronoBinding binding;
    Chronometer chronometer;
    ImageButton start, restart, pause, step;
    TextView stepShower;
    int stepCounter = 0;    // Count the number of steps
    private long lastPause; // Save the last chronoStamp

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        clockViewModel =
                new ViewModelProvider(this).get(ChronoViewModel.class);

        binding = FragmentChronoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initiate the chronometer
        chronometer = (Chronometer) getView().findViewById(R.id.chronometer);
        chronometer.setTextColor(getResources().getColor(R.color.purple_200,
                Objects.requireNonNull(getActivity()).getTheme()));

        // Start, Stop, Pause, create a Step for the Chronometer
        start = (ImageButton) getView().findViewById(R.id.start_chrono_button);
        restart = (ImageButton) getView().findViewById(R.id.restart_chrono_button);
        pause = (ImageButton) getView().findViewById(R.id.pause_chrono_button);
        step = (ImageButton) getView().findViewById(R.id.step_chrono_button);
        stepShower=(TextView) getView().findViewById(R.id.step_shower);

        // Perform click event on start button to start the chronometer
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requireView().performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                if (restart.getVisibility()==View.VISIBLE)
                    chronometer.setBase(chronometer.getBase() + SystemClock.elapsedRealtime() - lastPause);
                else
                    chronometer.setBase(SystemClock.elapsedRealtime());
                start.setVisibility(View.INVISIBLE);
                pause.setVisibility(View.VISIBLE);
                restart.setVisibility(View.VISIBLE);
                step.setVisibility(View.VISIBLE);
                chronometer.start();
            }
        });

        // Perform click event on stop button to stop the chronometer
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requireView().performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                pause.setVisibility(View.INVISIBLE);
                start.setVisibility(View.VISIBLE);
                step.setVisibility(View.INVISIBLE);
                lastPause = SystemClock.elapsedRealtime();
                chronometer.stop();
            }
        });

        // Perform click event on restart button to restart the chronometer
        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requireView().performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                restart.setVisibility(View.INVISIBLE);
                pause.setVisibility(View.INVISIBLE);
                start.setVisibility(View.VISIBLE);
                step.setVisibility(View.INVISIBLE);
                stepCounter=0;
                stepShower.setText(R.string.default_string);
                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.stop();
            }
        });

        //Perform click event on step button to create a partial of the chronometer
        step.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requireView().performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                stepCounter++;
                if(stepCounter>=11) {
                    Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(),
                            "Maximum number of steps reached", Toast.LENGTH_SHORT).show();
                    return;
                }
                stepShower.append("N."+stepCounter+" "+showElapsedTime()+"\n");
            }
        });

    }

    // Count the milliseconds to stamp the current step
    private String showElapsedTime(){
        long elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
        long millis = elapsedMillis % 1000;
        long second = (elapsedMillis / 1000) % 60;
        long minute = (elapsedMillis / (1000 * 60)) % 60;
        long hour = (elapsedMillis / (1000 * 60 * 60)) % 24;

        String time = String.format("%02d:%02d:%02d.%2d", hour, minute, second, millis);

        return time;
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        binding=null;
    }
}

