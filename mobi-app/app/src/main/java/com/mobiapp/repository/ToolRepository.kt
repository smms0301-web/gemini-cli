package com.mobiapp.repository

import com.mobiapp.data.entity.ToolEntity
import kotlinx.coroutines.flow.Flow

interface ToolRepository {
    fun getAllTools(): Flow<List<ToolEntity>>
    fun getToolById(id: Long): Flow<ToolEntity?>
    suspend fun insertTool(tool: ToolEntity): Long
    suspend fun updateTool(tool: ToolEntity)
    suspend fun deleteTool(tool: ToolEntity)
}
