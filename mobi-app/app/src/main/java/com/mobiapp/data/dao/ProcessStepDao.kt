package com.mobiapp.data.dao

import androidx.room.*
import com.mobiapp.data.entity.ProcessStepEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProcessStepDao {
    @Query("SELECT * FROM process_steps WHERE processId = :processId ORDER BY stepNumber ASC")
    fun getStepsForProcess(processId: Long): Flow<List<ProcessStepEntity>>

    @Query("SELECT * FROM process_steps WHERE processId = :processId ORDER BY stepNumber ASC")
    suspend fun getStepsForProcessSync(processId: Long): List<ProcessStepEntity>

    @Query("SELECT * FROM process_steps WHERE id = :id")
    suspend fun getById(id: Long): ProcessStepEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(step: ProcessStepEntity): Long

    @Update
    suspend fun update(step: ProcessStepEntity)

    @Delete
    suspend fun delete(step: ProcessStepEntity)

    @Query("DELETE FROM process_steps WHERE processId = :processId")
    suspend fun deleteAllForProcess(processId: Long)

    @Query("SELECT MAX(stepNumber) FROM process_steps WHERE processId = :processId")
    suspend fun getMaxStepNumber(processId: Long): Int?

    @Query("UPDATE process_steps SET isDone = :isDone, updatedAt = :now WHERE id = :id")
    suspend fun updateDoneStatus(id: Long, isDone: Boolean, now: Long = System.currentTimeMillis())
}
