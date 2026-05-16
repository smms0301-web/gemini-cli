package com.mobiapp.repository

import com.mobiapp.data.dao.ReminderDao
import com.mobiapp.data.entity.ReminderEntity
import javax.inject.Inject

class ReminderRepositoryImpl @Inject constructor(private val dao: ReminderDao) : ReminderRepository {
    override fun getAllReminders() = dao.getAllReminders()
    override fun getReminderById(id: Long) = dao.getReminderById(id)
    override suspend fun getEnabledReminders() = dao.getEnabledReminders()
    override suspend fun insertReminder(reminder: ReminderEntity) = dao.insertReminder(reminder)
    override suspend fun updateReminder(reminder: ReminderEntity) = dao.updateReminder(reminder)
    override suspend fun deleteReminder(reminder: ReminderEntity) = dao.deleteReminder(reminder)
}
