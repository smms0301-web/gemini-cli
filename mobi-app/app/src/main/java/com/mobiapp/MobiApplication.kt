package com.mobiapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MobiApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val channel = NotificationChannel(
            "reminders",
            "Reminders",
            NotificationManager.IMPORTANCE_HIGH
        ).apply { description = "Mobi App reminder notifications" }
        val nm = getSystemService(NotificationManager::class.java)
        nm.createNotificationChannel(channel)
    }
}
