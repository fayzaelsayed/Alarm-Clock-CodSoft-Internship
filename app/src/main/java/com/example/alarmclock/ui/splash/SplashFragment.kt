package com.example.alarmclock.ui.splash

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.alarmclock.R
import com.example.alarmclock.databinding.FragmentSplashBinding
import com.example.alarmclock.utils.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SplashFragment : BaseFragment(false) {
    private lateinit var binding: FragmentSplashBinding
    private lateinit var alarmText: Animation
    private lateinit var leftText: Animation
    private lateinit var rightText: Animation
    private lateinit var lottieAnimation: Animation


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSplashBinding.inflate(inflater, container, false)
        alarmText = AnimationUtils.loadAnimation(requireContext(), R.anim.from_top_to_bottom)
        leftText = AnimationUtils.loadAnimation(requireContext(), R.anim.from_left_to_right)
        rightText = AnimationUtils.loadAnimation(requireContext(), R.anim.from_right_to_left)
        lottieAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.from_bottom_to_top)

        CoroutineScope(Dispatchers.Main).launch {
            delay(1000)
            binding.tvAlarmClock.visibility = View.VISIBLE
            binding.tvAlarmClock.startAnimation(alarmText)
            binding.tvTick.visibility = View.VISIBLE
            binding.tvTick.startAnimation(leftText)
            binding.tvTock.visibility = View.VISIBLE
            binding.tvTock.startAnimation(rightText)
            delay(500)
            binding.lottieAnimationView.visibility = View.VISIBLE
            binding.lottieAnimationView.startAnimation(lottieAnimation)
            delay(3000)
            findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
        }

        return binding.root
    }

}