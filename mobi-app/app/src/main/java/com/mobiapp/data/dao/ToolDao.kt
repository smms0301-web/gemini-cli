package com.mobiapp.data.dao

import androidx.room.*
import com.mobiapp.data.entity.ToolEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ToolDao {
    @Query("SELECT * FROM tools ORDER BY name ASC")
    fun getAll(): Flow<List<ToolEntity>>

    @Query("SELECT * FROM tools WHERE id = :id")
    suspend fun getById(id: Long): ToolEntity?

    @Query("SELECT * FROM tools WHERE name LIKE '%' || :q || '%' OR description LIKE '%' || :q || '%' OR tags LIKE '%' || :q || '%'")
    suspend fun search(q: String): List<ToolEntity>

    @Query("SELECT * FROM tools ORDER BY name ASC")
    suspend fun getAllSync(): List<ToolEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tool: ToolEntity): Long

    @Update
    suspend fun update(tool: ToolEntity)

    @Delete
    suspend fun delete(tool: ToolEntity)

    @Query("SELECT COUNT(*) FROM tools")
    fun count(): Flow<Int>
}
