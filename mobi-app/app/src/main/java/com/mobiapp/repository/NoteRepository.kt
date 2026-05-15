package com.mobiapp.repository

import com.mobiapp.data.dao.NoteDao
import com.mobiapp.data.entity.NoteEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface NoteRepository {
    fun getAll(): Flow<List<NoteEntity>>
    suspend fun getById(id: Long): NoteEntity?
    suspend fun search(query: String): List<NoteEntity>
    suspend fun insert(note: NoteEntity): Long
    suspend fun update(note: NoteEntity)
    suspend fun delete(note: NoteEntity)
    suspend fun setPinned(id: Long, pinned: Boolean)
    fun count(): Flow<Int>
}

class NoteRepositoryImpl @Inject constructor(private val dao: NoteDao) : NoteRepository {
    override fun getAll() = dao.getAll()
    override suspend fun getById(id: Long) = dao.getById(id)
    override suspend fun search(query: String) = dao.search(query)
    override suspend fun insert(note: NoteEntity) = dao.insert(note)
    override suspend fun update(note: NoteEntity) = dao.update(note)
    override suspend fun delete(note: NoteEntity) = dao.delete(note)
    override suspend fun setPinned(id: Long, pinned: Boolean) = dao.setPinned(id, pinned)
    override fun count() = dao.count()
}
