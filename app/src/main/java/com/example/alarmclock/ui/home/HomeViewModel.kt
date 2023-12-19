package com.example.alarmclock.ui.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    fun displayCurrentTime(): String {
        val currentTime = Date()
        val timeFormat = SimpleDateFormat("HH:mm a", Locale.getDefault())
        return timeFormat.format(currentTime)
    }

    fun displayCurrentDate(): String {
        val currentDate = Date()
        val dateFormat = SimpleDateFormat("dd.MMMM yyyy", Locale.getDefault())
        return dateFormat.format(currentDate)

    }
}