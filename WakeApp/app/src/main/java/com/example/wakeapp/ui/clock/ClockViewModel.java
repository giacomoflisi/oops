package com.example.wakeapp.ui.clock;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ClockViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ClockViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Home");
    }

    public LiveData<String> getText() {
        return mText;
    }
}