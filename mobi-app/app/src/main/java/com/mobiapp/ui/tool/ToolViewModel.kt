package com.mobiapp.ui.tool

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobiapp.data.entity.ToolEntity
import com.mobiapp.repository.ToolRepository
import com.mobiapp.util.FuzzySearch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ToolViewModel @Inject constructor(private val repo: ToolRepository) : ViewModel() {

    val tools: StateFlow<List<ToolEntity>> = repo.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    /**
     * Multi-tag AND filter with fuzzy matching:
     * Each space-separated token must match at least one tag on the tool.
     * Single token matches name/description too.
     */
    val filtered: StateFlow<List<ToolEntity>> = combine(tools, _query) { list, q ->
        if (q.isBlank()) return@combine list
        val tokens = q.trim().lowercase().split("\\s+".toRegex()).filter { it.isNotBlank() }
        list.filter { tool ->
            val tags = tool.tags.split(",").map { it.trim().lowercase() }
            tokens.all { token ->
                tags.any { tag -> FuzzySearch.matches(token, tag) } ||
                FuzzySearch.matches(token, tool.name) ||
                FuzzySearch.matches(token, tool.description)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setQuery(q: String) { _query.value = q }

    suspend fun getById(id: Long) = repo.getById(id)

    fun save(tool: ToolEntity, onDone: () -> Unit) = viewModelScope.launch {
        if (tool.id == 0L) repo.insert(tool) else repo.update(tool.copy(updatedAt = System.currentTimeMillis()))
        onDone()
    }

    fun delete(tool: ToolEntity) = viewModelScope.launch { repo.delete(tool) }
}
