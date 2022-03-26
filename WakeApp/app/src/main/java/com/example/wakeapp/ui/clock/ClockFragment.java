package com.example.wakeapp.ui.clock;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.wakeapp.R;
import com.example.wakeapp.databinding.FragmentClockBinding;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

public class ClockFragment extends Fragment {

    private ClockViewModel clockViewModel;
    private FragmentClockBinding binding;
    TextClock digitalClock;
    Switch clockSwitch;
    FrameLayout analogClock;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        clockViewModel =
                new ViewModelProvider(this).get(ClockViewModel.class);

        binding = FragmentClockBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        digitalClock = (TextClock) view.findViewById(R.id.digitalClock);
        clockSwitch = (Switch) view.findViewById(R.id.switch_Analog_Digital);
        analogClock = (FrameLayout) view.findViewById(R.id.analogClock);
        clockSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    digitalClock.setVisibility(View.INVISIBLE);
                    analogClock.setVisibility(View.VISIBLE);
                }
                else{
                    digitalClock.setVisibility(View.VISIBLE);
                    analogClock.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}