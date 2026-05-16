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

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "mobi_app.db").build()

    @Provides fun provideProcessDao(db: AppDatabase): ProcessDao = db.processDao()
    @Provides fun provideReminderDao(db: AppDatabase): ReminderDao = db.reminderDao()
    @Provides fun providePromptDao(db: AppDatabase): PromptDao = db.promptDao()
    @Provides fun provideToolDao(db: AppDatabase): ToolDao = db.toolDao()
    @Provides fun provideNoteDao(db: AppDatabase): NoteDao = db.noteDao()
}
