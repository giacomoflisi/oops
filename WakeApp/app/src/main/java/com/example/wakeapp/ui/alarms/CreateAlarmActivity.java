package com.example.wakeapp.ui.alarms;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.example.wakeapp.MainActivity;
import com.example.wakeapp.R;
import com.example.wakeapp.SettingsActivity;
import com.example.wakeapp.databinding.FragmentClockBinding;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class CreateAlarmActivity extends AppCompatActivity {
    TimePicker timePicker;
    EditText title;
    Button scheduleAlarm;
    CheckBox recurring;
    CheckBox mon;
    CheckBox tue;
    CheckBox wed;
    CheckBox thu;
    CheckBox fri;
    CheckBox sat;
    CheckBox sun;
    LinearLayout recurringOptions;
    private AlarmRepository alarmRepository;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_createalarm);

        timePicker= (TimePicker) findViewById(R.id.fragment_createalarm_timePicker);
        title= (EditText) findViewById(R.id.fragment_createalarm_title);
        scheduleAlarm= (Button) findViewById(R.id.fragment_createalarm_scheduleAlarm);
        recurring= (CheckBox) findViewById(R.id.fragment_createalarm_recurring);
        mon= (CheckBox) findViewById(R.id.fragment_createalarm_checkMon);
        tue= (CheckBox) findViewById(R.id.fragment_createalarm_checkTue);
        wed= (CheckBox) findViewById(R.id.fragment_createalarm_checkWed);
        thu= (CheckBox) findViewById(R.id.fragment_createalarm_checkThu);
        fri= (CheckBox) findViewById(R.id.fragment_createalarm_checkFri);
        sat= (CheckBox) findViewById(R.id.fragment_createalarm_checkSat);
        sun= (CheckBox) findViewById(R.id.fragment_createalarm_checkSun);
        recurringOptions= (LinearLayout) findViewById(R.id.fragment_createalarm_recurring_options);

        if(recurring != null) {
            recurring.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        recurringOptions.setVisibility(View.VISIBLE);
                    } else {
                        recurringOptions.setVisibility(View.GONE);
                    }
                }
            });
        }

        if(scheduleAlarm != null) {
            scheduleAlarm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    scheduleAlarm();
                    //Toast.makeText(CreateAlarmActivity.this, "Alarm added!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void scheduleAlarm() {
        int alarmId = new Random().nextInt(Integer.MAX_VALUE);

        Alarm alarm = new Alarm(
                alarmId,
                TimePickerUtil.getTimePickerHour(timePicker),
                TimePickerUtil.getTimePickerMinute(timePicker),
                title.getText().toString(),
                System.currentTimeMillis(),
                true,
                recurring.isChecked(),
                mon.isChecked(),
                tue.isChecked(),
                wed.isChecked(),
                thu.isChecked(),
                fri.isChecked(),
                sat.isChecked(),
                sun.isChecked()
        );

        insert(alarm);

        alarm.schedule(getApplicationContext());
    }

    public void insert(Alarm alarm) {
        alarmRepository = new AlarmRepository(getApplication());
        alarmRepository.insert(alarm);
    }
}
