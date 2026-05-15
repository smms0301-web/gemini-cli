package com.mobiapp.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.mobiapp.MainActivity
import com.mobiapp.R

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getLongExtra(EXTRA_REMINDER_ID, -1L)
        val message = intent.getStringExtra(EXTRA_MESSAGE) ?: return
        val repeatMode = intent.getStringExtra(EXTRA_REPEAT_MODE) ?: "once"
        val weekdays = intent.getStringExtra(EXTRA_WEEKDAYS) ?: ""
        val timeMillis = intent.getLongExtra(EXTRA_TIME_MILLIS, 0L)

        showNotification(context, reminderId, message)

        // Re-schedule if repeating
        if (repeatMode != "once") {
            val reminder = com.mobiapp.data.entity.ReminderEntity(
                id = reminderId,
                message = message,
                timeMillis = timeMillis,
                repeatMode = repeatMode,
                weekdays = weekdays
            )
            ReminderScheduler.schedule(context, reminder)
        }
    }

    private fun showNotification(context: Context, id: Long, message: String) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, "Reminders", NotificationManager.IMPORTANCE_HIGH
            ).apply { description = "Mobi App reminders" }
            nm.createNotificationChannel(channel)
        }

        val tapIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("navigate_to", "reminders")
        }
        val pi = PendingIntent.getActivity(
            context, id.toInt(), tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("Mobi Reminder")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pi)
            .setAutoCancel(true)
            .build()

        nm.notify(id.toInt(), notification)
    }

    companion object {
        const val CHANNEL_ID = "mobi_reminders"
        const val EXTRA_REMINDER_ID = "reminder_id"
        const val EXTRA_MESSAGE = "message"
        const val EXTRA_REPEAT_MODE = "repeat_mode"
        const val EXTRA_WEEKDAYS = "weekdays"
        const val EXTRA_TIME_MILLIS = "time_millis"
    }
}
