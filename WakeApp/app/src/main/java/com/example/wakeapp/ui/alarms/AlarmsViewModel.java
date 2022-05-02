package com.example.wakeapp.ui.alarms;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.AndroidViewModel;

import java.util.List;

public class AlarmsViewModel extends AndroidViewModel {

    private AlarmRepository alarmRepository;
    private LiveData<List<Alarm>> alarmsLiveData;

    public AlarmsViewModel(@NonNull Application application) {
        super(application);

        alarmRepository = new AlarmRepository(application);
        alarmsLiveData = alarmRepository.getAlarmsLiveData();
    }

    public void update(Alarm alarm) {
        alarmRepository.update(alarm);
    }

    public void deleteAlarm (Alarm alarm){
        alarmRepository.delete(alarm);
    }

    public void deleteAllAlarms () { alarmRepository.deleteAll();}

    public LiveData<List<Alarm>> getAlarmsLiveData() {
        return alarmsLiveData;
    }

}
