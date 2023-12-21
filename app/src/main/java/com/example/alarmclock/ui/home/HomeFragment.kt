package com.example.alarmclock.ui.home

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.alarmclock.databinding.FragmentHomeBinding
import com.example.alarmclock.utils.BaseFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment : BaseFragment(true) {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()
    private val SYSTEM_ALERT_WINDOW_PERMISSION_CODE = 1
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.tvTime.text = viewModel.displayCurrentTime()
        binding.tvDate.text = viewModel.displayCurrentDate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(requireContext())) {
            requestSystemAlertWindowPermission()
        } else {
        }
        return binding.root
    }
    private fun requestSystemAlertWindowPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + requireActivity().packageName)
            )
            startActivityForResult(intent, SYSTEM_ALERT_WINDOW_PERMISSION_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SYSTEM_ALERT_WINDOW_PERMISSION_CODE) {
            // Check if the user granted the "Draw over other apps" permission.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(requireContext())) {
                // The permission is granted. Your code to handle this scenario goes here.
            } else {
                Toast.makeText(requireContext(),"please accept the permission to work perfectly", Toast.LENGTH_LONG).show()
            }
        }
    }
}