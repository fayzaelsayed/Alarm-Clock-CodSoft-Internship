package com.example.alarmclock.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.alarmclock.R
import com.example.alarmclock.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.myNavHostFragment) as NavHostFragment
        navController = navHostFragment.navController

        binding.chipAppBar.setItemSelected(R.id.home, true)
        binding.chipAppBar.setBackgroundResource(R.drawable.corner)
        binding.chipAppBar.setOnItemSelectedListener { id ->
            when (id) {
                R.id.home -> navController.navigate(R.id.homeFragment)
                R.id.management -> navController.navigate(R.id.alarmManagementFragment)
            }
        }
    }
    fun isBottomAppBarVisible(isVisible: Boolean) {
        if (isVisible) {
            binding.chipAppBar.visibility = View.VISIBLE
        } else {
            binding.chipAppBar.visibility = View.GONE
        }
    }
}