package com.example.wakeapp.ui.chrono;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ChronoViewModel extends ViewModel{

    private MutableLiveData<String> mText;

    public ChronoViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("TODO: implement a clock view here");
    }

    public LiveData<String> getText() {
        return mText;
    }

}
