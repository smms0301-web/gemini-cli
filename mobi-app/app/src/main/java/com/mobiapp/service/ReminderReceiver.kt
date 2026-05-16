package com.mobiapp.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.mobiapp.MainActivity
import com.mobiapp.data.entity.ReminderEntity

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getLongExtra("reminder_id", 0L)
        val title = intent.getStringExtra("title") ?: "Reminder"
        val description = intent.getStringExtra("description") ?: ""
        val isRepeating = intent.getBooleanExtra("is_repeating", false)
        val intervalMs = intent.getLongExtra("repeat_interval_ms", 0L)

        val tapIntent = Intent(context, MainActivity::class.java)
        val tapPi = PendingIntent.getActivity(
            context, id.toInt(), tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, "reminders")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(description)
            .setContentIntent(tapPi)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(id.toInt(), notification)

        if (isRepeating && intervalMs > 0) {
            val next = ReminderEntity(
                id = id, title = title, description = description,
                triggerTimeMs = System.currentTimeMillis() + intervalMs,
                isRepeating = true, repeatIntervalMs = intervalMs, isEnabled = true
            )
            ReminderScheduler.schedule(context, next)
        }
    }
}
