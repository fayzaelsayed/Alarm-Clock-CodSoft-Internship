package com.example.alarmclock.alarmmanager

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.alarmclock.database.AlarmEntity
import com.example.alarmclock.ui.alarm.AlarmDialogActivity
import com.google.gson.Gson

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
            try {
                val entity = intent?.getParcelableExtra<AlarmEntity>("alarm1") ?: return
                Log.i("rrrrrrrrrr", "onReceive:${entity.id} ")
                val intentToAlarmDialog = Intent(context, AlarmDialogActivity::class.java)
                intentToAlarmDialog.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intentToAlarmDialog.putExtra("alarm2", entity)
                intentToAlarmDialog.putExtra("music", true)
                context?.startActivity(intentToAlarmDialog)
            } catch (e: Exception) {
                Log.i("aaaaaaaaaaaa", "onReceive: catch $e")
            }
    }
}