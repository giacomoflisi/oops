package com.example.wakeapp.ui.alarms;

import android.os.Build;
import android.widget.TimePicker;

public final class TimePickerUtil {
    public static int getTimePickerHour(TimePicker tp) {
        return tp.getHour();
    }

    public static int getTimePickerMinute(TimePicker tp) {
        return tp.getMinute();
    }
}