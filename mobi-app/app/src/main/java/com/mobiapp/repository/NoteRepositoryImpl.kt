package com.mobiapp.repository

import com.mobiapp.data.dao.NoteDao
import com.mobiapp.data.entity.NoteEntity
import javax.inject.Inject

class NoteRepositoryImpl @Inject constructor(private val dao: NoteDao) : NoteRepository {
    override fun getAllNotes() = dao.getAllNotes()
    override fun getNoteById(id: Long) = dao.getNoteById(id)
    override suspend fun insertNote(note: NoteEntity) = dao.insertNote(note)
    override suspend fun updateNote(note: NoteEntity) = dao.updateNote(note)
    override suspend fun deleteNote(note: NoteEntity) = dao.deleteNote(note)
}
