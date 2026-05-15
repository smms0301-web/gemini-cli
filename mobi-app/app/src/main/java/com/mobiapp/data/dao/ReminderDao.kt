package com.mobiapp.data.dao

import androidx.room.*
import com.mobiapp.data.entity.ReminderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminders ORDER BY timeMillis ASC")
    fun getAll(): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE id = :id")
    suspend fun getById(id: Long): ReminderEntity?

    @Query("SELECT * FROM reminders WHERE isEnabled = 1")
    suspend fun getAllEnabled(): List<ReminderEntity>

    @Query("SELECT * FROM reminders WHERE message LIKE '%' || :query || '%'")
    suspend fun search(query: String): List<ReminderEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reminder: ReminderEntity): Long

    @Update
    suspend fun update(reminder: ReminderEntity)

    @Delete
    suspend fun delete(reminder: ReminderEntity)

    @Query("UPDATE reminders SET isEnabled = :enabled WHERE id = :id")
    suspend fun setEnabled(id: Long, enabled: Boolean)

    @Query("UPDATE reminders SET nextFireTimeMillis = :nextFireTime WHERE id = :id")
    suspend fun updateNextFireTime(id: Long, nextFireTime: Long)

    @Query("SELECT COUNT(*) FROM reminders WHERE isEnabled = 1")
    fun activeCount(): Flow<Int>
}
