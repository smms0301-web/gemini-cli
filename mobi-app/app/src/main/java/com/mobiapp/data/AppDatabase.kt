package com.mobiapp.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mobiapp.data.dao.*
import com.mobiapp.data.entity.*

@Database(
    entities = [
        ProcessEntity::class,
        ProcessStepEntity::class,
        ReminderEntity::class,
        PromptEntity::class,
        ToolEntity::class,
        NoteEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun processDao(): ProcessDao
    abstract fun processStepDao(): ProcessStepDao
    abstract fun reminderDao(): ReminderDao
    abstract fun promptDao(): PromptDao
    abstract fun toolDao(): ToolDao
    abstract fun noteDao(): NoteDao

    companion object {
        const val DATABASE_NAME = "mobiapp_db"
    }
}
