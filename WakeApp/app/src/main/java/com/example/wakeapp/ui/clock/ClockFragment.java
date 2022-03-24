package com.example.wakeapp.ui.clock;

import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.wakeapp.R;
import com.example.wakeapp.databinding.FragmentClockBinding;

import java.util.Date;

public class ClockFragment extends Fragment {

    private ClockViewModel clockViewModel;
    private FragmentClockBinding binding;
    TextView digitalClock;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        clockViewModel =
                new ViewModelProvider(this).get(ClockViewModel.class);

        binding = FragmentClockBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}