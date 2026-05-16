package com.mobiapp.repository

import com.mobiapp.data.dao.ToolDao
import com.mobiapp.data.entity.ToolEntity
import javax.inject.Inject

class ToolRepositoryImpl @Inject constructor(private val dao: ToolDao) : ToolRepository {
    override fun getAllTools() = dao.getAllTools()
    override fun getToolById(id: Long) = dao.getToolById(id)
    override suspend fun insertTool(tool: ToolEntity) = dao.insertTool(tool)
    override suspend fun updateTool(tool: ToolEntity) = dao.updateTool(tool)
    override suspend fun deleteTool(tool: ToolEntity) = dao.deleteTool(tool)
}
