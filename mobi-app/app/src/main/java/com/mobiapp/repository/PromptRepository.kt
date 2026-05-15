package com.mobiapp.repository

import com.mobiapp.data.dao.PromptDao
import com.mobiapp.data.entity.PromptEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface PromptRepository {
    fun getAll(): Flow<List<PromptEntity>>
    suspend fun getById(id: Long): PromptEntity?
    suspend fun search(query: String): List<PromptEntity>
    suspend fun insert(prompt: PromptEntity): Long
    suspend fun update(prompt: PromptEntity)
    suspend fun delete(prompt: PromptEntity)
    fun count(): Flow<Int>
}

class PromptRepositoryImpl @Inject constructor(private val dao: PromptDao) : PromptRepository {
    override fun getAll() = dao.getAll()
    override suspend fun getById(id: Long) = dao.getById(id)
    override suspend fun search(query: String) = dao.search(query)
    override suspend fun insert(prompt: PromptEntity) = dao.insert(prompt)
    override suspend fun update(prompt: PromptEntity) = dao.update(prompt)
    override suspend fun delete(prompt: PromptEntity) = dao.delete(prompt)
    override fun count() = dao.count()
}
