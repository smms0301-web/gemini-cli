package com.mobiapp.repository

import com.mobiapp.data.dao.ProcessDao
import com.mobiapp.data.entity.ProcessEntity
import com.mobiapp.data.entity.ProcessStepEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProcessRepositoryImpl @Inject constructor(private val dao: ProcessDao) : ProcessRepository {
    override fun getAllProcesses() = dao.getAllProcesses()
    override fun getProcessById(id: Long) = dao.getProcessById(id)
    override suspend fun insertProcess(process: ProcessEntity) = dao.insertProcess(process)
    override suspend fun updateProcess(process: ProcessEntity) = dao.updateProcess(process)
    override suspend fun deleteProcess(process: ProcessEntity) = dao.deleteProcess(process)
    override fun getStepsForProcess(processId: Long) = dao.getStepsForProcess(processId)
    override fun getStepById(id: Long) = dao.getStepById(id)
    override suspend fun insertStep(step: ProcessStepEntity) = dao.insertStep(step)
    override suspend fun updateStep(step: ProcessStepEntity) = dao.updateStep(step)
    override suspend fun deleteStep(step: ProcessStepEntity) = dao.deleteStep(step)
    override suspend fun getMaxStepNumber(processId: Long) = dao.getMaxStepNumber(processId)
}
