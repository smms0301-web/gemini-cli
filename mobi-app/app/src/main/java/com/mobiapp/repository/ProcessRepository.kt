package com.mobiapp.repository

import com.mobiapp.data.entity.ProcessEntity
import com.mobiapp.data.entity.ProcessStepEntity
import kotlinx.coroutines.flow.Flow

interface ProcessRepository {
    fun getAllProcesses(): Flow<List<ProcessEntity>>
    fun getProcessById(id: Long): Flow<ProcessEntity?>
    suspend fun insertProcess(process: ProcessEntity): Long
    suspend fun updateProcess(process: ProcessEntity)
    suspend fun deleteProcess(process: ProcessEntity)
    fun getStepsForProcess(processId: Long): Flow<List<ProcessStepEntity>>
    fun getStepById(id: Long): Flow<ProcessStepEntity?>
    suspend fun insertStep(step: ProcessStepEntity): Long
    suspend fun updateStep(step: ProcessStepEntity)
    suspend fun deleteStep(step: ProcessStepEntity)
    suspend fun getMaxStepNumber(processId: Long): Int?
}
