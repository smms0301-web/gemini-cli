package com.mobiapp.ui.note

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobiapp.data.entity.NoteEntity
import com.mobiapp.repository.NoteRepository
import com.mobiapp.util.FuzzySearch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val repo: NoteRepository
) : ViewModel() {

    val query = MutableStateFlow("")

    private val allNotes = repo.getAllNotes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notes: StateFlow<List<NoteEntity>> = query.combine(allNotes) { q, list ->
        if (q.isBlank()) list
        else list.filter { FuzzySearch.matches(q, "${it.title} ${it.content}") }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getNote(id: Long) = repo.getNoteById(id)

    fun saveNote(note: NoteEntity, onDone: () -> Unit) = viewModelScope.launch {
        if (note.id == 0L) repo.insertNote(note)
        else repo.updateNote(note.copy(updatedAt = System.currentTimeMillis()))
        onDone()
    }

    fun togglePin(note: NoteEntity) = viewModelScope.launch {
        repo.updateNote(note.copy(isPinned = !note.isPinned))
    }

    fun deleteNote(note: NoteEntity) = viewModelScope.launch { repo.deleteNote(note) }
}
