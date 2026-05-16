package com.mobiapp.data.dao

import androidx.room.*
import com.mobiapp.data.entity.ProcessEntity
import com.mobiapp.data.entity.ProcessStepEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProcessDao {
    @Query("SELECT * FROM processes ORDER BY updatedAt DESC")
    fun getAllProcesses(): Flow<List<ProcessEntity>>

    @Query("SELECT * FROM processes WHERE id = :id")
    fun getProcessById(id: Long): Flow<ProcessEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProcess(process: ProcessEntity): Long

    @Update
    suspend fun updateProcess(process: ProcessEntity)

    @Delete
    suspend fun deleteProcess(process: ProcessEntity)

    @Query("SELECT * FROM process_steps WHERE processId = :processId ORDER BY stepNumber ASC")
    fun getStepsForProcess(processId: Long): Flow<List<ProcessStepEntity>>

    @Query("SELECT * FROM process_steps WHERE id = :id")
    fun getStepById(id: Long): Flow<ProcessStepEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStep(step: ProcessStepEntity): Long

    @Update
    suspend fun updateStep(step: ProcessStepEntity)

    @Delete
    suspend fun deleteStep(step: ProcessStepEntity)

    @Query("SELECT MAX(stepNumber) FROM process_steps WHERE processId = :processId")
    suspend fun getMaxStepNumber(processId: Long): Int?
}
