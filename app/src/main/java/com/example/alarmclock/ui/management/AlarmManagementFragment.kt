package com.example.alarmclock.ui.management

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.alarmclock.R
import com.example.alarmclock.database.AlarmEntity
import com.example.alarmclock.databinding.FragmentAlarmManagementBinding
import com.example.alarmclock.utils.Action
import com.example.alarmclock.utils.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlarmManagementFragment : BaseFragment(true) {
    private lateinit var binding: FragmentAlarmManagementBinding
    private lateinit var alarmAdapter: AlarmAdapter
    private lateinit var alarmsList: MutableList<AlarmEntity>
    private val viewModel: AlarmManagementViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAlarmManagementBinding.inflate(inflater, container, false)
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
            builder.dismiss()
        }

        noButton.setOnClickListener {
            builder.dismiss()
        }

        builder.show()
    }

    fun updateState(alarmEntity: AlarmEntity) {
        if (alarmEntity.alarmState == "on") {
            viewModel.updateAlarmState(alarmEntity.id, "off")
        } else {
            viewModel.updateAlarmState(alarmEntity.id, "on")
        }
    }
}