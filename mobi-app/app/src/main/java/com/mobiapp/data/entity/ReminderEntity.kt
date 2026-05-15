package com.mobiapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val message: String,
    val category: String = "General",
    val timeMillis: Long,         // Time of day as millis since midnight
    val repeatMode: String,       // "once", "daily", "weekdays"
    val weekdays: String = "",    // comma-separated: "1,2,3,4,5" (Mon-Fri)
    val isEnabled: Boolean = true,
    val nextFireTimeMillis: Long = 0L,
    val createdAt: Long = System.currentTimeMillis()
)
