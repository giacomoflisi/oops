package com.example.wakeapp.ui.clock;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ClockViewModel extends ViewModel{

    private MutableLiveData<String> mText;

    public ClockViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("TODO: implement a clock view here");
    }

    public LiveData<String> getText() {
        return mText;
    }

}
