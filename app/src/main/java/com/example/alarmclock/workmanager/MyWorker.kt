package com.example.alarmclock.workmanager

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.alarmclock.database.AlarmEntity
import com.example.alarmclock.ui.alarm.AlarmDialogActivity
import com.google.gson.Gson
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class MyWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters
) :
    CoroutineWorker(context, workerParameters) {
    override suspend fun doWork(): Result {
        try {
            val alarmString = inputData.getString("Entity")
            val gson = Gson()
            val entity = gson.fromJson(alarmString, AlarmEntity::class.java)
            val intent = Intent(applicationContext, AlarmDialogActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("alertEntity", entity)
            intent.putExtra("music", true)
            applicationContext.startActivity(intent)
            Log.i("doWorks", "doWork: sucess ")
            return Result.success()
        } catch (e: Exception) {
            Log.i("doWork", "doWork: fail")
            return Result.failure()
        }
    }
}