package com.example.alarmclock.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AlarmDatabaseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAlarm(alarmEntity: AlarmEntity)

    @Query("SELECT * FROM alarm_clock_table")
    fun getAllAlarms(): LiveData<List<AlarmEntity>>

    @Query("SELECT * FROM alarm_clock_table")
    fun getAlarms(): List<AlarmEntity>

    @Delete
    fun deleteAlarm(alarmEntity: AlarmEntity)

    @Query("UPDATE alarm_clock_table SET alarmState = :state WHERE alarmId = :key ")
    fun updateAlarmState(key: String, state: String)

    @Update
    fun updateAlarm(alarmEntity: AlarmEntity)

    @Query("UPDATE alarm_clock_table SET alarmDate = :date WHERE id = :key ")
    fun updateAlarmDate(key: Int, date:String)

    @Query("UPDATE alarm_clock_table SET alarmState = :state WHERE alarmId = :key ")
    fun updateAlarmStateWithId(key: String, state:String)
}