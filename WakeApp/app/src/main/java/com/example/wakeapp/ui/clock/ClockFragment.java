package com.example.wakeapp.ui.clock;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.example.wakeapp.R;
import com.example.wakeapp.databinding.FragmentClockBinding;

public class ClockFragment extends Fragment {

    private ClockViewModel clockViewModel;
    private FragmentClockBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        clockViewModel =
                new ViewModelProvider(this).get(ClockViewModel.class);

        binding = FragmentClockBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textClock;
        clockViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        binding=null;
    }
}

