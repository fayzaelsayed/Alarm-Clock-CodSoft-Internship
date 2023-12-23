package com.example.alarmclock.ui.management

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.alarmclock.R
import com.example.alarmclock.alarmmanager.AndroidAlarmScheduler
import com.example.alarmclock.alarmmanager.BootReceiver
import com.example.alarmclock.database.AlarmEntity
import com.example.alarmclock.databinding.FragmentAlarmManagementBinding
import com.example.alarmclock.utils.Action
import com.example.alarmclock.utils.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class AlarmManagementFragment : BaseFragment(true) {
    private lateinit var binding: FragmentAlarmManagementBinding
    private lateinit var alarmAdapter: AlarmAdapter
    private lateinit var alarmsList: MutableList<AlarmEntity>
    private val viewModel: AlarmManagementViewModel by viewModels()
    private lateinit var scheduler: AndroidAlarmScheduler
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAlarmManagementBinding.inflate(inflater, container, false)
        scheduler = AndroidAlarmScheduler(requireContext())
        binding.ibAdd.setOnClickListener {
            findNavController().navigate(AlarmManagementFragmentDirections.actionAlarmManagementFragmentToAlarmSettingFragment(null))
        }
        alarmsList = ArrayList()
        setUpAdapter()

        viewModel.getAllAlarmsFromDatabase().observe(viewLifecycleOwner) { list ->
            list?.let {
                alarmsList.clear()
                alarmsList.addAll(list)
                val newList = ArrayList<AlarmEntity>()
                newList.addAll(alarmsList)
                alarmAdapter.submitList(newList)
                if (alarmsList.isEmpty()) {
                    binding.lottieAnimationView.visibility = View.VISIBLE
                    binding.tvNoAlarms.visibility = View.VISIBLE
                } else {
                    binding.lottieAnimationView.visibility = View.GONE
                    binding.tvNoAlarms.visibility = View.GONE
                }
            }
        }

        return binding.root
    }

    private fun setUpAdapter() {
        alarmAdapter = AlarmAdapter()
        binding.rvAlarms.layoutManager =
            LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        binding.rvAlarms.adapter = alarmAdapter

        alarmAdapter.setOnButtonClickListener(object : AlarmAdapter.OnButtonClickListener {
            override fun onButtonClick(action: Action, alarmEntity: AlarmEntity) {
                when (action) {
                    Action.DELETE -> {
                        showDialog(alarmEntity)
                    }
                    Action.UPDATE_STATE -> {
                        updateState(alarmEntity)
                    }
                    Action.EDIT_ALARM -> {
                        findNavController().navigate(
                            AlarmManagementFragmentDirections.actionAlarmManagementFragmentToAlarmSettingFragment(
                                alarmEntity
                            )
                        )
                    }
                    else -> {}
                }
            }

        })

    }

    private fun showDialog(alarmEntity: AlarmEntity) {
        val builder = Dialog(requireContext())
        builder.setContentView(R.layout.delete_dialog)
        builder.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(),
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        builder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        builder.window?.setGravity(Gravity.CENTER)

        val yesButton = builder.findViewById<Button>(R.id.btn_yes_delete)
        val noButton = builder.findViewById<Button>(R.id.btn_no_delete)


        yesButton.setOnClickListener {
            viewModel.deleteAlarmFromDatabase(alarmEntity)
            scheduler.cancel(alarmEntity)
            builder.dismiss()
        }

        noButton.setOnClickListener {
            builder.dismiss()
        }

        builder.show()
    }

    fun updateState(alarmEntity: AlarmEntity) {
        if (alarmEntity.alarmState == "on") {
            viewModel.updateAlarmState(alarmEntity.alarmId, "off")
            scheduler.cancel(alarmEntity)
        } else {
            val comparisonResult = compareDates(alarmEntity.alarmDate, alarmEntity.alarmTime)
            when {
                comparisonResult < 0 -> {
                    //in future
                    viewModel.updateAlarmState(alarmEntity.alarmId, "on")
                    scheduler.schedule(alarmEntity)

            }
                comparisonResult > 0 -> {
                    //in paste
                    viewModel.updateAlarmState(alarmEntity.alarmId, "on")
                    viewModel.updateDate(alarmEntity.id, getTomorrowDate(alarmEntity.alarmDate))
                    scheduler.schedule(alarmEntity)
            }
                else -> {
                    viewModel.updateAlarmState(alarmEntity.alarmId, "on")
                    scheduler.schedule(alarmEntity)
            }
            }

        }
    }


    private fun compareDates(otherDateString: String, otherTimeString: String): Int {
        val dateFormat = SimpleDateFormat("dd.MMMM yyyy HH:mm a", Locale.getDefault())
        val currentDateTimeString = SimpleDateFormat("dd.MMMM yyyy HH:mm a", Locale.getDefault()).format(Date())
        val currentDate = dateFormat.parse(currentDateTimeString)
        val otherDateTimeString = "$otherDateString $otherTimeString"
        val otherDate = dateFormat.parse(otherDateTimeString)

        return currentDate.compareTo(otherDate)
    }


    private fun getTomorrowDate(inputDateString: String): String {
        val inputDateFormat = SimpleDateFormat("dd.MMMM yyyy", Locale.getDefault())
        val inputDate: Date = inputDateFormat.parse(inputDateString)!!
        val calendar = Calendar.getInstance()
        calendar.time = inputDate
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        return SimpleDateFormat("dd.MMMM yyyy", Locale.getDefault()).format(calendar.time)
    }



}