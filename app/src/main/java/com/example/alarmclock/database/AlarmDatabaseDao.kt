package com.example.alarmclock.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AlarmDatabaseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAlarm(alarmEntity: AlarmEntity)

    @Query("SELECT * FROM alarm_clock_table")
    fun getAllAlarms(): LiveData<List<AlarmEntity>>

    @Delete
    fun deleteAlarm(alarmEntity: AlarmEntity)

    @Query("UPDATE alarm_clock_table SET alarmState = :state WHERE id = :key ")
    fun updateAlarmState(key: String, state: String)

    @Update
    fun updateAlarm(alarmEntity: AlarmEntity)


}