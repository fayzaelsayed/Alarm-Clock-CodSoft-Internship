package com.example.alarmclock.utils

import androidx.fragment.app.Fragment
import com.example.alarmclock.ui.MainActivity

abstract class BaseFragment(private val isVisible: Boolean): Fragment() {
    override fun onStart() {
        super.onStart()
        (requireActivity() as MainActivity).isBottomAppBarVisible(isVisible)
    }

}