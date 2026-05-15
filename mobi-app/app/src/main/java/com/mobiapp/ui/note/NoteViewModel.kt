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
class NoteViewModel @Inject constructor(private val repo: NoteRepository) : ViewModel() {

    val notes: StateFlow<List<NoteEntity>> = repo.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    val filtered: StateFlow<List<NoteEntity>> = combine(notes, _query) { list, q ->
        if (q.isBlank()) list
        else list.filter { n -> FuzzySearch.matches(q, n.title) || q.lowercase() in n.body.lowercase() }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setQuery(q: String) { _query.value = q }

    suspend fun getById(id: Long) = repo.getById(id)

    fun save(note: NoteEntity, onDone: (Long) -> Unit) = viewModelScope.launch {
        val id = if (note.id == 0L) repo.insert(note) else { repo.update(note.copy(updatedAt = System.currentTimeMillis())); note.id }
        onDone(id)
    }

    fun delete(note: NoteEntity) = viewModelScope.launch { repo.delete(note) }

    fun togglePin(note: NoteEntity) = viewModelScope.launch { repo.setPinned(note.id, !note.isPinned) }
}
