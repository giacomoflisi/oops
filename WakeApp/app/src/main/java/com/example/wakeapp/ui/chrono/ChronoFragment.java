package com.example.wakeapp.ui.chrono;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.example.wakeapp.databinding.FragmentChronoBinding;

public class ChronoFragment extends Fragment {

    private ChronoViewModel clockViewModel;
    private FragmentChronoBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        clockViewModel =
                new ViewModelProvider(this).get(ChronoViewModel.class);

        binding = FragmentChronoBinding.inflate(inflater, container, false);
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

