package com.mobiapp.data.dao

import androidx.room.*
import com.mobiapp.data.entity.ToolEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ToolDao {
    @Query("SELECT * FROM tools ORDER BY isFavorite DESC, name ASC")
    fun getAllTools(): Flow<List<ToolEntity>>

    @Query("SELECT * FROM tools WHERE id = :id")
    fun getToolById(id: Long): Flow<ToolEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTool(tool: ToolEntity): Long

    @Update
    suspend fun updateTool(tool: ToolEntity)

    @Delete
    suspend fun deleteTool(tool: ToolEntity)
}
