package com.mobiapp.data.dao

import androidx.room.*
import com.mobiapp.data.entity.ProcessEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProcessDao {
    @Query("SELECT * FROM processes ORDER BY updatedAt DESC")
    fun getAll(): Flow<List<ProcessEntity>>

    @Query("SELECT * FROM processes WHERE id = :id")
    suspend fun getById(id: Long): ProcessEntity?

    @Query("SELECT * FROM processes WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' ORDER BY updatedAt DESC")
    suspend fun search(query: String): List<ProcessEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(process: ProcessEntity): Long

    @Update
    suspend fun update(process: ProcessEntity)

    @Delete
    suspend fun delete(process: ProcessEntity)

    @Query("SELECT COUNT(*) FROM processes")
    fun count(): Flow<Int>
}
