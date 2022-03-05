package com.example.wakeapp.ui.tomato;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TomatoViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public TomatoViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("TODO: implement pomodoro timer technique figure out how to change the icon");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
