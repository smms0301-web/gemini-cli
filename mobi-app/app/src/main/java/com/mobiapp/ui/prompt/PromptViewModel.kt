package com.mobiapp.ui.prompt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobiapp.data.entity.PromptEntity
import com.mobiapp.repository.PromptRepository
import com.mobiapp.util.FuzzySearch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PromptViewModel @Inject constructor(private val repo: PromptRepository) : ViewModel() {

    val prompts: StateFlow<List<PromptEntity>> = repo.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    val filtered: StateFlow<List<PromptEntity>> = combine(prompts, _query) { list, q ->
        if (q.isBlank()) list
        else list.filter { p -> FuzzySearch.matches(q, p.title) || FuzzySearch.matches(q, p.category) || q.lowercase() in p.promptText.lowercase() }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setQuery(q: String) { _query.value = q }

    suspend fun getById(id: Long) = repo.getById(id)

    fun save(prompt: PromptEntity, onDone: () -> Unit) = viewModelScope.launch {
        if (prompt.id == 0L) repo.insert(prompt) else repo.update(prompt.copy(updatedAt = System.currentTimeMillis()))
        onDone()
    }

    fun delete(prompt: PromptEntity) = viewModelScope.launch { repo.delete(prompt) }
}
