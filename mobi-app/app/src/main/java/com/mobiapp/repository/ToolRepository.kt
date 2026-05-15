package com.mobiapp.repository

import com.mobiapp.data.dao.ToolDao
import com.mobiapp.data.entity.ToolEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface ToolRepository {
    fun getAll(): Flow<List<ToolEntity>>
    suspend fun getById(id: Long): ToolEntity?
    suspend fun search(query: String): List<ToolEntity>
    suspend fun getAllSync(): List<ToolEntity>
    suspend fun insert(tool: ToolEntity): Long
    suspend fun update(tool: ToolEntity)
    suspend fun delete(tool: ToolEntity)
    fun count(): Flow<Int>
}

class ToolRepositoryImpl @Inject constructor(private val dao: ToolDao) : ToolRepository {
    override fun getAll() = dao.getAll()
    override suspend fun getById(id: Long) = dao.getById(id)
    override suspend fun search(query: String) = dao.search(query)
    override suspend fun getAllSync() = dao.getAllSync()
    override suspend fun insert(tool: ToolEntity) = dao.insert(tool)
    override suspend fun update(tool: ToolEntity) = dao.update(tool)
    override suspend fun delete(tool: ToolEntity) = dao.delete(tool)
    override fun count() = dao.count()
}
