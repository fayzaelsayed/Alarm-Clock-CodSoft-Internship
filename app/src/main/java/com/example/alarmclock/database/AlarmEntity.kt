package com.example.alarmclock.database

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "alarm_clock_table")
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val alarmId: String,
    var alarmName: String,
    var alarmTime: String,
    var alarmDate: String,
    var workRequest: Long,
    var alarmTone: String,
    var alarmState: String
) : Parcelable
