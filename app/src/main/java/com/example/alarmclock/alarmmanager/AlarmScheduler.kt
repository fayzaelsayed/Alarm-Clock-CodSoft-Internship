package com.example.alarmclock.alarmmanager

import com.example.alarmclock.database.AlarmEntity

interface AlarmScheduler {
    fun schedule(alarmEntity: AlarmEntity)
    fun cancel(alarmEntity: AlarmEntity)
    fun snooze(alarmEntity: AlarmEntity)
}