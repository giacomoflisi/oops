package com.example.wakeapp.ui.alarms;

import androidx.lifecycle.LiveData;
import androidx.room.*;

import java.util.List;

// Data Access Object (DAO) that defines the methods to interact with the data in the Room Database

@Dao
public interface AlarmDao {
    @Insert
    void insert(Alarm alarm);

    @Query("DELETE FROM alarm_table")
    void deleteAll();

    @Query("SELECT * FROM alarm_table ORDER BY created ASC")
    LiveData<List<Alarm>> getAlarms();

    @Update
    void update(Alarm alarm);

    @Delete
    void delete(Alarm alarm);
}