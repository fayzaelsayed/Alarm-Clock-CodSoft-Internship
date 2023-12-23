package com.example.alarmclock.ui.alarm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alarmclock.database.AlarmDatabaseDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AlarmDialogViewModel @Inject constructor(private val dao: AlarmDatabaseDao): ViewModel() {
    fun updateAlarmState(id: String, state: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                dao.updateAlarmStateWithId(id, state)
            } catch (e: Exception) {
                Log.i("updateAlarmState", "updateAlarmState: $e ")
            }
        }
    }
}