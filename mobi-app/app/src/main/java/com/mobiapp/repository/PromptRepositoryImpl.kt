package com.mobiapp.repository

import com.mobiapp.data.dao.PromptDao
import com.mobiapp.data.entity.PromptEntity
import javax.inject.Inject

class PromptRepositoryImpl @Inject constructor(private val dao: PromptDao) : PromptRepository {
    override fun getAllPrompts() = dao.getAllPrompts()
    override fun getPromptById(id: Long) = dao.getPromptById(id)
    override suspend fun insertPrompt(prompt: PromptEntity) = dao.insertPrompt(prompt)
    override suspend fun updatePrompt(prompt: PromptEntity) = dao.updatePrompt(prompt)
    override suspend fun deletePrompt(prompt: PromptEntity) = dao.deletePrompt(prompt)
}
