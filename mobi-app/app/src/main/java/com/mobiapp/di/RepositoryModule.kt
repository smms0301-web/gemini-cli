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
    @Binds @Singleton abstract fun bindProcessRepo(impl: ProcessRepositoryImpl): ProcessRepository
    @Binds @Singleton abstract fun bindReminderRepo(impl: ReminderRepositoryImpl): ReminderRepository
    @Binds @Singleton abstract fun bindPromptRepo(impl: PromptRepositoryImpl): PromptRepository
    @Binds @Singleton abstract fun bindToolRepo(impl: ToolRepositoryImpl): ToolRepository
    @Binds @Singleton abstract fun bindNoteRepo(impl: NoteRepositoryImpl): NoteRepository
}
