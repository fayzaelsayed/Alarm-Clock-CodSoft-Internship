package com.example.alarmclock.ui.setting

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.alarmclock.R
import com.example.alarmclock.alarmmanager.AndroidAlarmScheduler
import com.example.alarmclock.database.AlarmEntity
import com.example.alarmclock.databinding.FragmentAlarmSettingBinding
import com.example.alarmclock.utils.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class AlarmSettingFragment : BaseFragment(false) {
    private lateinit var binding: FragmentAlarmSettingBinding
    private val viewModel: AlarmSettingViewModel by viewModels()
    private lateinit var mediaPlayer: MediaPlayer
    private val tones = arrayOf(R.raw.placeholder_tone, R.raw.tone1, R.raw.tone2, R.raw.tone3)
    private val toneNames = arrayOf("Alarm tone", "tone1", "tone2", "tone3")
    private var setYear = 0
    private var setMonth = 0
    private var setDay = 0
    private var setHour = 0
    private var setMinute = 0
    private var numberOfWork = -1L
    private lateinit var args: AlarmSettingFragmentArgs

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAlarmSettingBinding.inflate(inflater, container, false)
        val scheduler = AndroidAlarmScheduler(requireContext())
        args = AlarmSettingFragmentArgs.fromBundle(requireArguments())
        binding.ibClock.setOnClickListener {
            openTimePicker(binding.tvSelectedTime)
        }

        displayCurrentDate()
        getTheSelectedDate()

        val calender = Calendar.getInstance()
        val lastDayOfMonth = calender.getActualMaximum(Calendar.DAY_OF_MONTH)
        binding.radioGroup.setOnCheckedChangeListener { _, id ->
            when (id) {
                R.id.rb_once -> numberOfWork = -1L
                R.id.rb_repeat_daily -> numberOfWork = 1L
                R.id.rb_repeat_monthly -> numberOfWork = lastDayOfMonth.toLong()
                R.id.rb_repeat_yearly -> numberOfWork = 365L
            }
        }

        val toneAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.tones_array,
            android.R.layout.simple_spinner_item
        )
        toneAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinner.adapter = toneAdapter

        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.placeholder_tone)

        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            @SuppressLint("SetTextI18n")
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.stop()
                    mediaPlayer.prepare()
                }
                val selectedTone = tones[position]
                mediaPlayer = MediaPlayer.create(requireContext(), selectedTone)
                mediaPlayer.start()
                binding.tvAlarmTone.text = toneNames[position]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        binding.btnAdd.setOnClickListener {
            if (args.alarm != null) {
                updateAlarm(args.alarm!!)
                scheduler.cancel(args.alarm!!)
                scheduler.schedule(args.alarm!!)
                findNavController().navigateUp()
            } else if (binding.tvSelectedTime.text.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Choose the time first", Toast.LENGTH_LONG).show()
            } else {
                addAndSetAlarm(scheduler)
                findNavController().navigateUp()
            }
        }

        binding.btnCancel.setOnClickListener {
            findNavController().navigate(R.id.action_alarmSettingFragment_to_alarmManagementFragment)
        }

        if (args.alarm != null) {
            fillOutTheViewsWithOldData()
            binding.btnAdd.text = "Update"
        }


        return binding.root
    }

    private fun displayCurrentDate() {
        val currentDate = Date()
        val dateFormat = SimpleDateFormat("dd.MMMM yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(currentDate)
        binding.tvSelectedDate.text = formattedDate

        setYear = Calendar.getInstance().get(Calendar.YEAR)
        setMonth = Calendar.getInstance().get(Calendar.MONTH)
        setDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        Log.i("displayCurrentDate", "displayCurrentDate: $setMonth ")
    }

    private fun getTheSelectedDate() {
        val currentDate = Calendar.getInstance()
        binding.calendarView.minDate = currentDate.timeInMillis
        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, month, dayOfMonth)
            setYear = year
            setMonth = month
            setDay = dayOfMonth
            Log.i("getTheSelectedDate", "getTheSelectedDate: $setMonth")
            if (selectedDate.before(currentDate)) {
                binding.calendarView.date = currentDate.timeInMillis
            }

            val dateFormat = SimpleDateFormat("dd.MMMM yyyy", Locale.getDefault())
            val formattedDate = dateFormat.format(selectedDate.time)
            binding.tvSelectedDate.text = formattedDate
        }
    }

    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun openTimePicker(timeTextView: TextView) {
        val calender = Calendar.getInstance()
        val timePickerDialogListener = TimePickerDialog.OnTimeSetListener { _, hour, minute  ->
            calender.set(
                setYear,
                setMonth,
                setDay,
                hour,
                minute
            )
            setHour = hour
            setMinute = minute
            if (calender.timeInMillis >= Calendar.getInstance().timeInMillis) {
                timeTextView.text = SimpleDateFormat("HH:mm a").format(calender.time)

            } else {
                Toast.makeText(requireContext(), "time unavailable", Toast.LENGTH_LONG).show()
            }

        }
        val timePickerDialog = TimePickerDialog(
            requireContext(),
            timePickerDialogListener,
            Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
            Calendar.getInstance().get(Calendar.MINUTE) + 1,
            true
        )
        timePickerDialog.show()
    }

    private fun addAndSetAlarm(scheduler: AndroidAlarmScheduler) {
        val alarm = AlarmEntity(
            id = 0,
            alarmName = binding.edtAlarmName.text.toString(),
            alarmTime = binding.tvSelectedTime.text.toString(),
            alarmDate = binding.tvSelectedDate.text.toString(),
            workRequest = numberOfWork,
            alarmTone = binding.tvAlarmTone.text.toString(),
            alarmState = "on",
            alarmId = UUID.randomUUID().toString()
        )
        viewModel.insertAlarmToDatabase(alarm)
        scheduler.schedule(alarm)
    }

    private fun fillOutTheViewsWithOldData() {
        binding.tvSelectedTime.text = args.alarm?.alarmTime
        binding.tvSelectedDate.text = args.alarm?.alarmDate
        binding.edtAlarmName.setText(args.alarm?.alarmName)
        numberOfWork = args.alarm?.workRequest!!

    }

    private fun updateAlarm(alarm: AlarmEntity) {
        alarm.alarmTime = binding.tvSelectedTime.text.toString()
        alarm.alarmDate = binding.tvSelectedDate.text.toString()
        alarm.alarmName = binding.edtAlarmName.text.toString()
        alarm.alarmTone = binding.tvAlarmTone.text.toString()

        viewModel.updateAlarm(alarm)
        viewModel.updateAlarmState(alarm.alarmId,"on")
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop()
    }
}