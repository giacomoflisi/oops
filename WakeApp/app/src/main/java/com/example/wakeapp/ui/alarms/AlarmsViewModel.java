package com.example.wakeapp.ui.alarms;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AlarmsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public AlarmsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("TODO: implement alarms setup with scroller and plus button");
    }

    public LiveData<String> getText() {
        return mText;
    }
}