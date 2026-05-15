package com.mobiapp.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.mobiapp.data.entity.ReminderEntity
import java.util.Calendar

object ReminderScheduler {

    fun schedule(context: Context, reminder: ReminderEntity) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = buildIntent(context, reminder)
        val pendingIntent = PendingIntent.getBroadcast(
            context, reminder.id.toInt(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerTime = nextTriggerTime(reminder)
        if (triggerTime <= 0) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
        } else {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
        }
    }

    fun cancel(context: Context, reminderId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, reminderId.toInt(), intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        ) ?: return
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }

    fun nextTriggerTime(reminder: ReminderEntity): Long {
        val cal = Calendar.getInstance()
        val timeOfDay = reminder.timeMillis
        val hour = ((timeOfDay / 3600000) % 24).toInt()
        val minute = ((timeOfDay / 60000) % 60).toInt()

        cal.set(Calendar.HOUR_OF_DAY, hour)
        cal.set(Calendar.MINUTE, minute)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)

        return when (reminder.repeatMode) {
            "once" -> {
                if (cal.timeInMillis <= System.currentTimeMillis()) cal.add(Calendar.DAY_OF_YEAR, 1)
                cal.timeInMillis
            }
            "daily" -> {
                if (cal.timeInMillis <= System.currentTimeMillis()) cal.add(Calendar.DAY_OF_YEAR, 1)
                cal.timeInMillis
            }
            "weekdays" -> {
                val days = reminder.weekdays.split(",").mapNotNull { it.trim().toIntOrNull() }
                if (days.isEmpty()) return 0L
                repeat(7) {
                    val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) // 1=Sun..7=Sat
                    // Convert to Mon=1..Sun=7
                    val d = if (dayOfWeek == Calendar.SUNDAY) 7 else dayOfWeek - 1
                    if (d in days && cal.timeInMillis > System.currentTimeMillis()) return cal.timeInMillis
                    cal.add(Calendar.DAY_OF_YEAR, 1)
                }
                cal.timeInMillis
            }
            else -> 0L
        }
    }

    private fun buildIntent(context: Context, reminder: ReminderEntity): Intent =
        Intent(context, ReminderReceiver::class.java).apply {
            putExtra(ReminderReceiver.EXTRA_REMINDER_ID, reminder.id)
            putExtra(ReminderReceiver.EXTRA_MESSAGE, reminder.message)
            putExtra(ReminderReceiver.EXTRA_REPEAT_MODE, reminder.repeatMode)
            putExtra(ReminderReceiver.EXTRA_WEEKDAYS, reminder.weekdays)
            putExtra(ReminderReceiver.EXTRA_TIME_MILLIS, reminder.timeMillis)
        }
}
