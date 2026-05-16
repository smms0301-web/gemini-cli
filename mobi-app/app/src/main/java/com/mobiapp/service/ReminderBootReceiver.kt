package com.mobiapp.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.mobiapp.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.room.Room

class ReminderBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        val db = Room.databaseBuilder(context, AppDatabase::class.java, "mobi_app.db").build()
        CoroutineScope(Dispatchers.IO).launch {
            val reminders = db.reminderDao().getEnabledReminders()
            val now = System.currentTimeMillis()
            for (r in reminders) {
                if (r.triggerTimeMs > now) {
                    ReminderScheduler.schedule(context, r)
                }
            }
            db.close()
        }
    }
}
