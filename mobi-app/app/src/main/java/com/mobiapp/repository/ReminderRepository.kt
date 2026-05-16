package com.mobiapp.repository

import com.mobiapp.data.entity.ReminderEntity
import kotlinx.coroutines.flow.Flow

interface ReminderRepository {
    fun getAllReminders(): Flow<List<ReminderEntity>>
    fun getReminderById(id: Long): Flow<ReminderEntity?>
    suspend fun getEnabledReminders(): List<ReminderEntity>
    suspend fun insertReminder(reminder: ReminderEntity): Long
    suspend fun updateReminder(reminder: ReminderEntity)
    suspend fun deleteReminder(reminder: ReminderEntity)
}
