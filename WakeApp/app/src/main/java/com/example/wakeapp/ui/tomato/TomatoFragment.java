package com.example.wakeapp.ui.tomato;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.example.wakeapp.R;
import com.example.wakeapp.databinding.FragmentTomatoBinding;

public class TomatoFragment extends Fragment implements Button.OnClickListener{

    private TomatoViewModel tomatoViewModel;
    private FragmentTomatoBinding binding;
    private ImageButton mImageButton;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        tomatoViewModel =
                new ViewModelProvider(this).get(TomatoViewModel.class);

        binding = FragmentTomatoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mImageButton = (ImageButton) mImageButton.findViewById(R.id.play_button);
        mImageButton.setOnClickListener(this);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    //play timer click listener
    @Override
    public void onClick(View view){


    }
}
