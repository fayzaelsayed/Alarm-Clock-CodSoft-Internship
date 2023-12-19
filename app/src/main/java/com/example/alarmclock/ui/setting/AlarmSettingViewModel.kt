package com.example.alarmclock.ui.setting

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.example.alarmclock.database.AlarmDatabaseDao
import com.example.alarmclock.database.AlarmEntity
import com.example.alarmclock.workmanager.MyWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import com.google.gson.Gson
import java.time.Duration

@HiltViewModel
class AlarmSettingViewModel @Inject constructor(private val dao: AlarmDatabaseDao) : ViewModel() {
    private var workRequest: WorkRequest? = null
    fun insertAlarmToDatabase(alarmEntity: AlarmEntity){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                dao.insertAlarm(alarmEntity)
            }catch (e: Exception){
                Log.i("insertAlarmToDatabase", "insertAlarmToDatabase: $e ")
            }
        }
    }

    fun updateAlarm(alarmEntity: AlarmEntity){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                dao.updateAlarm(alarmEntity)
            }catch (e:Exception){
                Log.i("updateAlarm", "updateAlarm: $e")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun myWorkManagerRequest(
        hMonth: Int,
        hDay: Int,
        hHour: Int,
        hMinute: Int,
        context: Context,
        alarm: AlarmEntity,
        numberOfWork: Long
    ) {

        val currentDate = Calendar.getInstance()
        val dueDate = Calendar.getInstance()

        // Set the desired month, day, and hour
        dueDate.set(Calendar.MONTH, hMonth)
        dueDate.set(Calendar.DAY_OF_MONTH, hDay)
        dueDate.set(Calendar.HOUR_OF_DAY, hHour)
        dueDate.set(Calendar.MINUTE, hMinute)
        dueDate.set(Calendar.SECOND, 0)


        val timeDiff = dueDate.timeInMillis - currentDate.timeInMillis
        val gson = Gson()
        val alarmString = gson.toJson(alarm)
        val data = Data.Builder().putString("Entity", alarmString).build()

        if (numberOfWork == -1L) {
            // Schedule the work
            workRequest = OneTimeWorkRequest.Builder(MyWorker::class.java)
                .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
                .setBackoffCriteria(BackoffPolicy.LINEAR, Duration.ofSeconds(15))
                .setInputData(data)
                .addTag(alarm.id)
                .build()
            WorkManager.getInstance(context).enqueue(workRequest as OneTimeWorkRequest)
        } else {
            workRequest = PeriodicWorkRequest.Builder(
                MyWorker::class.java,
                numberOfWork,
                TimeUnit.DAYS
            ).setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
                .addTag(alarm.id)
                .setInputData(data)
                .build()
            WorkManager.getInstance(context).enqueue(workRequest as PeriodicWorkRequest)
        }
    }

}