package com.mobiapp.di

import android.content.Context
import androidx.room.Room
import com.mobiapp.data.AppDatabase
import com.mobiapp.data.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): AppDatabase =
        Room.databaseBuilder(ctx, AppDatabase::class.java, AppDatabase.DATABASE_NAME).build()

    @Provides fun provideProcessDao(db: AppDatabase): ProcessDao = db.processDao()
    @Provides fun provideProcessStepDao(db: AppDatabase): ProcessStepDao = db.processStepDao()
    @Provides fun provideReminderDao(db: AppDatabase): ReminderDao = db.reminderDao()
    @Provides fun providePromptDao(db: AppDatabase): PromptDao = db.promptDao()
    @Provides fun provideToolDao(db: AppDatabase): ToolDao = db.toolDao()
    @Provides fun provideNoteDao(db: AppDatabase): NoteDao = db.noteDao()
}
