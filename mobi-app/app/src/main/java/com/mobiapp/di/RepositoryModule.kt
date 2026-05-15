package com.mobiapp.di

import com.mobiapp.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds @Singleton abstract fun bindProcessRepository(impl: ProcessRepositoryImpl): ProcessRepository
    @Binds @Singleton abstract fun bindReminderRepository(impl: ReminderRepositoryImpl): ReminderRepository
    @Binds @Singleton abstract fun bindPromptRepository(impl: PromptRepositoryImpl): PromptRepository
    @Binds @Singleton abstract fun bindToolRepository(impl: ToolRepositoryImpl): ToolRepository
    @Binds @Singleton abstract fun bindNoteRepository(impl: NoteRepositoryImpl): NoteRepository
}
