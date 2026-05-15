package com.mobiapp.repository

import com.mobiapp.data.dao.ReminderDao
import com.mobiapp.data.entity.ReminderEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface ReminderRepository {
    fun getAll(): Flow<List<ReminderEntity>>
    suspend fun getById(id: Long): ReminderEntity?
    suspend fun getAllEnabled(): List<ReminderEntity>
    suspend fun search(query: String): List<ReminderEntity>
    suspend fun insert(reminder: ReminderEntity): Long
    suspend fun update(reminder: ReminderEntity)
    suspend fun delete(reminder: ReminderEntity)
    suspend fun setEnabled(id: Long, enabled: Boolean)
    suspend fun updateNextFireTime(id: Long, nextFireTime: Long)
    fun activeCount(): Flow<Int>
}

class ReminderRepositoryImpl @Inject constructor(
    private val dao: ReminderDao
) : ReminderRepository {
    override fun getAll() = dao.getAll()
    override suspend fun getById(id: Long) = dao.getById(id)
    override suspend fun getAllEnabled() = dao.getAllEnabled()
    override suspend fun search(query: String) = dao.search(query)
    override suspend fun insert(reminder: ReminderEntity) = dao.insert(reminder)
    override suspend fun update(reminder: ReminderEntity) = dao.update(reminder)
    override suspend fun delete(reminder: ReminderEntity) = dao.delete(reminder)
    override suspend fun setEnabled(id: Long, enabled: Boolean) = dao.setEnabled(id, enabled)
    override suspend fun updateNextFireTime(id: Long, nextFireTime: Long) = dao.updateNextFireTime(id, nextFireTime)
    override fun activeCount() = dao.activeCount()
}
