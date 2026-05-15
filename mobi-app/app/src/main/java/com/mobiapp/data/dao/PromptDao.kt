package com.mobiapp.data.dao

import androidx.room.*
import com.mobiapp.data.entity.PromptEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PromptDao {
    @Query("SELECT * FROM prompts ORDER BY updatedAt DESC")
    fun getAll(): Flow<List<PromptEntity>>

    @Query("SELECT * FROM prompts WHERE id = :id")
    suspend fun getById(id: Long): PromptEntity?

    @Query("SELECT * FROM prompts WHERE title LIKE '%' || :q || '%' OR promptText LIKE '%' || :q || '%' OR category LIKE '%' || :q || '%'")
    suspend fun search(q: String): List<PromptEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(prompt: PromptEntity): Long

    @Update
    suspend fun update(prompt: PromptEntity)

    @Delete
    suspend fun delete(prompt: PromptEntity)

    @Query("SELECT COUNT(*) FROM prompts")
    fun count(): Flow<Int>
}
