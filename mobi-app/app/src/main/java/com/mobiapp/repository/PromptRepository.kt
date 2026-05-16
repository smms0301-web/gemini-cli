package com.mobiapp.repository

import com.mobiapp.data.entity.PromptEntity
import kotlinx.coroutines.flow.Flow

interface PromptRepository {
    fun getAllPrompts(): Flow<List<PromptEntity>>
    fun getPromptById(id: Long): Flow<PromptEntity?>
    suspend fun insertPrompt(prompt: PromptEntity): Long
    suspend fun updatePrompt(prompt: PromptEntity)
    suspend fun deletePrompt(prompt: PromptEntity)
}
