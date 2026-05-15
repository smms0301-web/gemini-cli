package com.mobiapp.repository

import com.mobiapp.data.dao.ProcessDao
import com.mobiapp.data.dao.ProcessStepDao
import com.mobiapp.data.entity.ProcessEntity
import com.mobiapp.data.entity.ProcessStepEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface ProcessRepository {
    fun getAll(): Flow<List<ProcessEntity>>
    suspend fun getById(id: Long): ProcessEntity?
    suspend fun search(query: String): List<ProcessEntity>
    suspend fun insert(process: ProcessEntity): Long
    suspend fun update(process: ProcessEntity)
    suspend fun delete(process: ProcessEntity)
    fun count(): Flow<Int>

    fun getSteps(processId: Long): Flow<List<ProcessStepEntity>>
    suspend fun getStepsSync(processId: Long): List<ProcessStepEntity>
    suspend fun getStepById(id: Long): ProcessStepEntity?
    suspend fun insertStep(step: ProcessStepEntity): Long
    suspend fun updateStep(step: ProcessStepEntity)
    suspend fun deleteStep(step: ProcessStepEntity)
    suspend fun getNextStepNumber(processId: Long): Int
    suspend fun toggleStepDone(id: Long, isDone: Boolean)
}

class ProcessRepositoryImpl @Inject constructor(
    private val processDao: ProcessDao,
    private val stepDao: ProcessStepDao
) : ProcessRepository {
    override fun getAll() = processDao.getAll()
    override suspend fun getById(id: Long) = processDao.getById(id)
    override suspend fun search(query: String) = processDao.search(query)
    override suspend fun insert(process: ProcessEntity) = processDao.insert(process)
    override suspend fun update(process: ProcessEntity) = processDao.update(process)
    override suspend fun delete(process: ProcessEntity) = processDao.delete(process)
    override fun count() = processDao.count()

    override fun getSteps(processId: Long) = stepDao.getStepsForProcess(processId)
    override suspend fun getStepsSync(processId: Long) = stepDao.getStepsForProcessSync(processId)
    override suspend fun getStepById(id: Long) = stepDao.getById(id)
    override suspend fun insertStep(step: ProcessStepEntity) = stepDao.insert(step)
    override suspend fun updateStep(step: ProcessStepEntity) = stepDao.update(step)
    override suspend fun deleteStep(step: ProcessStepEntity) = stepDao.delete(step)
    override suspend fun getNextStepNumber(processId: Long): Int =
        (stepDao.getMaxStepNumber(processId) ?: 0) + 1
    override suspend fun toggleStepDone(id: Long, isDone: Boolean) =
        stepDao.updateDoneStatus(id, isDone)
}
