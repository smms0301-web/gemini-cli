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
        if (intent.action != Intent.ACTION_BOOT_COMPLETED &&
            intent.action != Intent.ACTION_MY_PACKAGE_REPLACED) return

        CoroutineScope(Dispatchers.IO).launch {
            val db = Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.DATABASE_NAME).build()
            val reminders = db.reminderDao().getAllEnabled()
            reminders.forEach { reminder ->
                ReminderScheduler.schedule(context, reminder)
            }
            db.close()
        }
    }
}
