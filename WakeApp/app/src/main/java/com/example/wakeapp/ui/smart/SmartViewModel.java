package com.example.wakeapp.ui.smart;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SmartViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public SmartViewModel() {
        mText = new MutableLiveData<>();
    }

    public LiveData<String> getText() {
        return mText;
    }
}