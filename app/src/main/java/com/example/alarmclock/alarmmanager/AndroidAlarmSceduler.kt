package com.example.alarmclock.alarmmanager

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.alarmclock.database.AlarmEntity
import java.text.SimpleDateFormat
import java.util.*

class AndroidAlarmScheduler(private val context: Context) : AlarmScheduler {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    override fun schedule(alarmEntity: AlarmEntity) {

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("alarm1", alarmEntity)
        }

        if (alarmEntity.workRequest == -1L) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                convertDateTimeToMilliseconds(alarmEntity.alarmDate, alarmEntity.alarmTime),
                PendingIntent.getBroadcast(
                    context,
                    alarmEntity.id,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
                )
            )
        } else {
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                convertDateTimeToMilliseconds(alarmEntity.alarmDate, alarmEntity.alarmTime),
                AlarmManager.INTERVAL_DAY * alarmEntity.workRequest,
                PendingIntent.getBroadcast(
                    context,
                    alarmEntity.id,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
                )
            )
        }


    }

    override fun cancel(alarmEntity: AlarmEntity) {
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                alarmEntity.id.toInt(),
                Intent(context, AlarmReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
        )
    }

    override fun snooze(alarmEntity: AlarmEntity) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("alarm1", alarmEntity)
        }
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            convertDateTimeToMilliseconds(alarmEntity.alarmDate, alarmEntity.alarmTime),
            5 * 60 * 1000,
            PendingIntent.getBroadcast(
                context,
                alarmEntity.id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
        )
    }

    private fun convertDateTimeToMilliseconds(dateString: String, timeString: String): Long {
        val dateTimeString = "$dateString $timeString"
        val dateFormat = SimpleDateFormat("dd.MMMM yyyy HH:mm a", Locale.getDefault())
        val date = dateFormat.parse(dateTimeString)
        return date?.time ?: 0L
    }


}