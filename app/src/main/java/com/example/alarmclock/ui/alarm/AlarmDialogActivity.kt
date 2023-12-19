package com.example.alarmclock.ui.alarm

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.databinding.DataBindingUtil
import androidx.work.WorkManager
import com.example.alarmclock.R
import com.example.alarmclock.database.AlarmEntity
import com.example.alarmclock.databinding.ActivityAlarmDialogBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlarmDialogActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAlarmDialogBinding
    private var entity: AlarmEntity? = null
    private val viewModel: AlarmDialogViewModel by viewModels()
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        entity = intent.getParcelableExtra("alertEntity")
        binding = DataBindingUtil.setContentView(this, R.layout.activity_alarm_dialog)
        val isFromWork = intent.getBooleanExtra("music", true)

        binding.notificationText.text = "${entity?.alarmName}"
        binding.notificationTitle.text = "${entity?.alarmTime}"

        binding.btnSnooze.setOnClickListener {
            //stopMediaPlayer()
            createNotification(entity!!)
            finish()
        }

        binding.btnDismissAlert.setOnClickListener {
           // stopMediaPlayer()
            if (entity?.workRequest == -1L) {
                viewModel.updateAlarmState(entity!!.id, "off")
            }

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(1)
            WorkManager.getInstance(this.applicationContext).cancelAllWorkByTag(
                entity!!.id
            )
            finish()
        }


    }
    companion object {
        const val CHANNEL_ID = "channel_id"
    }


    private fun createNotification(alarm: AlarmEntity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "Alarm Clock"
            val channelImportance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, channelName, channelImportance)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
        val intent = Intent(applicationContext, AlarmDialogActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.putExtra("alertEntity", alarm)
        intent.putExtra("music", false)
        val pendingIntent: PendingIntent =
            // Use FLAG_IMMUTABLE for targeting Android 12 (API level 31) and above
            PendingIntent.getActivity(
                applicationContext,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
            )


        val notificationBuilder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle("Alarm Clock")
            .setContentText("Snooze")
            .setSmallIcon(R.drawable.alarm)
            .setContentIntent(pendingIntent)
            .setAutoCancel(false)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setOngoing(true)

        // Show the notification
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notificationBuilder.build())
    }
}