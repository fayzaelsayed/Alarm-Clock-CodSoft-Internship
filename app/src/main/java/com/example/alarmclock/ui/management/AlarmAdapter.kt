package com.example.alarmclock.ui.management

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.alarmclock.R
import com.example.alarmclock.database.AlarmEntity
import com.example.alarmclock.databinding.AlarmItemBinding
import com.example.alarmclock.utils.Action

class AlarmAdapter : ListAdapter<AlarmEntity, AlarmAdapter.AlarmViewHolder>(DiffCallback()) {
    private lateinit var buttonClickListener: OnButtonClickListener
    fun setOnButtonClickListener(listener: OnButtonClickListener) {
        buttonClickListener = listener
    }

    interface OnButtonClickListener {
        fun onButtonClick(action: Action, alarmEntity: AlarmEntity)
    }

    inner class AlarmViewHolder(private val binding: AlarmItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(alarmEntity: AlarmEntity) {
            binding.apply {
                alarm = alarmEntity
                when (alarmEntity.alarmState) {
                    "on" -> {
                        switchButton.isChecked = true
                        cvAlarmItem.setBackgroundColor(cvAlarmItem.context.getColor(R.color.green_0))
                    }
                    "off" -> {
                        switchButton.isChecked = false
                        cvAlarmItem.setBackgroundColor(cvAlarmItem.context.getColor(R.color.light_green))
                    }
                }
                if(alarmEntity.alarmName.isEmpty()){
                    binding.tvAlarmName.visibility = View.GONE
                }else{
                    binding.tvAlarmName.visibility = View.VISIBLE
                }
                ibDelete.setOnClickListener {
                    buttonClickListener.onButtonClick(Action.DELETE, alarmEntity)
                }

                switchButton.setOnClickListener {
                    buttonClickListener.onButtonClick(Action.UPDATE_STATE, alarmEntity)
                }

                cvAlarmItem.setOnClickListener {
                    buttonClickListener.onButtonClick(Action.EDIT_ALARM, alarmEntity)
                }

            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<AlarmEntity>() {
        override fun areItemsTheSame(oldItem: AlarmEntity, newItem: AlarmEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: AlarmEntity,
            newItem: AlarmEntity
        ): Boolean {
            return oldItem.toString() == newItem.toString()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val binding =
            AlarmItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AlarmViewHolder(binding)
    }


    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        val currentItem = getItem(position)
        currentItem?.let {
            holder.bind(currentItem)
        }
    }


}