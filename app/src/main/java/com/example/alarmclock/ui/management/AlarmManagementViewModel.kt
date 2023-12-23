package com.example.alarmclock.ui.management

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alarmclock.database.AlarmDatabaseDao
import com.example.alarmclock.database.AlarmEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlarmManagementViewModel @Inject constructor(private val dao: AlarmDatabaseDao) :
    ViewModel() {

    fun getAllAlarmsFromDatabase(): LiveData<List<AlarmEntity>> {
        return dao.getAllAlarms()
    }

    fun deleteAlarmFromDatabase(alarmEntity: AlarmEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                dao.deleteAlarm(alarmEntity)
            } catch (e: Exception) {
                Log.i("deleteAlarmFromDatabase", "deleteAlarmFromDatabase: $e")
            }
        }
    }

    fun updateAlarmState(id: String, state: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                dao.updateAlarmState(id, state)
            } catch (e: Exception) {
                Log.i("updateAlarmState", "updateAlarmState: $e ")
            }
        }
    }

    fun updateDate(id: Int, date:String){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                dao.updateAlarmDate(id, date)
            }catch (e:Exception){
                Log.i("updateDate", "updateDate: $e")
            }
        }
    }
}