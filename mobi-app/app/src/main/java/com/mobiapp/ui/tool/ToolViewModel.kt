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
class ToolViewModel @Inject constructor(
    private val repo: ToolRepository
) : ViewModel() {

    val query = MutableStateFlow("")

    private val allTools = repo.getAllTools()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tools: StateFlow<List<ToolEntity>> = query.combine(allTools) { q, list ->
        if (q.isBlank()) list
        else {
            val tokens = q.trim().lowercase().split("\\s+".toRegex())
            list.filter { tool ->
                val haystack = "${tool.name} ${tool.description} ${tool.tags} ${tool.category}"
                FuzzySearch.matchesAll(tokens, haystack)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getTool(id: Long) = repo.getToolById(id)

    fun saveTool(tool: ToolEntity, onDone: () -> Unit) = viewModelScope.launch {
        if (tool.id == 0L) repo.insertTool(tool)
        else repo.updateTool(tool)
        onDone()
    }

    fun toggleFavorite(tool: ToolEntity) = viewModelScope.launch {
        repo.updateTool(tool.copy(isFavorite = !tool.isFavorite))
    }

    fun deleteTool(tool: ToolEntity) = viewModelScope.launch { repo.deleteTool(tool) }
}
