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
class PromptViewModel @Inject constructor(
    private val repo: PromptRepository
) : ViewModel() {

    val query = MutableStateFlow("")

    private val allPrompts = repo.getAllPrompts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val prompts: StateFlow<List<PromptEntity>> = query.combine(allPrompts) { q, list ->
        if (q.isBlank()) list
        else list.filter { FuzzySearch.matches(q, "${it.title} ${it.content} ${it.tags} ${it.category}") }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getPrompt(id: Long) = repo.getPromptById(id)

    fun savePrompt(prompt: PromptEntity, onDone: () -> Unit) = viewModelScope.launch {
        if (prompt.id == 0L) repo.insertPrompt(prompt)
        else repo.updatePrompt(prompt.copy(updatedAt = System.currentTimeMillis()))
        onDone()
    }

    fun toggleFavorite(prompt: PromptEntity) = viewModelScope.launch {
        repo.updatePrompt(prompt.copy(isFavorite = !prompt.isFavorite))
    }

    fun deletePrompt(prompt: PromptEntity) = viewModelScope.launch { repo.deletePrompt(prompt) }
}
