package com.mobiapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val triggerTimeMs: Long,
    val isRepeating: Boolean = false,
    val repeatIntervalMs: Long = 0L,
    val isEnabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
