package com.example.wakeapp.ui.tomato;

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
import com.example.wakeapp.R;
import com.example.wakeapp.databinding.FragmentTomatoBinding;

public class TomatoFragment extends Fragment {

    private TomatoViewModel tomatoViewModel;
    private FragmentTomatoBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        tomatoViewModel =
                new ViewModelProvider(this).get(TomatoViewModel.class);

        binding = FragmentTomatoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textTomato;
        tomatoViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
