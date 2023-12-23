package com.example.alarmclock.alarmmanager

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.alarmclock.database.AlarmDatabaseDao
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver: BroadcastReceiver() {
    @Inject
    lateinit var dao: AlarmDatabaseDao

    @Inject
    lateinit var application: Application
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            CoroutineScope(Dispatchers.IO).launch {
                val scheduler = AndroidAlarmScheduler(application)
                val alarms = dao.getAlarms()
                for (alarm in alarms) {
                    scheduler.schedule(alarm)
                }
            }
        }
    }
}