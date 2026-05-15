package com.mobiapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobiapp.data.entity.*
import com.mobiapp.repository.*
import com.mobiapp.util.FuzzySearch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchResult(
    val id: Long,
    val module: String,       // "Process", "Reminder", "Prompt", "Tool", "Note"
    val title: String,
    val snippet: String,
    val tags: String = ""
)

data class HomeUiState(
    val processCount: Int = 0,
    val reminderCount: Int = 0,
    val promptCount: Int = 0,
    val toolCount: Int = 0,
    val noteCount: Int = 0,
    val searchQuery: String = "",
    val searchResults: Map<String, List<SearchResult>> = emptyMap(),
    val isSearching: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val processRepo: ProcessRepository,
    private val reminderRepo: ReminderRepository,
    private val promptRepo: PromptRepository,
    private val toolRepo: ToolRepository,
    private val noteRepo: NoteRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    private val _counts = MutableStateFlow(HomeUiState())

    val uiState: StateFlow<HomeUiState> = _counts.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                processRepo.count(),
                reminderRepo.activeCount(),
                promptRepo.count(),
                toolRepo.count(),
                noteRepo.count()
            ) { pc, rc, prc, tc, nc ->
                _counts.value.copy(processCount = pc, reminderCount = rc, promptCount = prc, toolCount = tc, noteCount = nc)
            }.collect { _counts.value = it }
        }

        viewModelScope.launch {
            _query.debounce(150).collect { q -> if (q.isNotBlank()) performSearch(q) else clearSearch() }
        }
    }

    fun onSearchQueryChange(q: String) {
        _query.value = q
        _counts.update { it.copy(searchQuery = q, isSearching = q.isNotBlank()) }
        if (q.isBlank()) clearSearch()
    }

    private fun clearSearch() {
        _counts.update { it.copy(searchResults = emptyMap(), isSearching = false) }
    }

    private suspend fun performSearch(query: String) {
        val results = mutableMapOf<String, MutableList<SearchResult>>()

        // Process
        val processes = processRepo.search(query)
        val fuzzyProcesses = processRepo.getAll().first().filter { p ->
            FuzzySearch.matches(query, p.title) || FuzzySearch.matches(query, p.category) || FuzzySearch.matches(query, p.siteTag)
        }
        (processes + fuzzyProcesses).distinctBy { it.id }.take(5).forEach { p ->
            results.getOrPut("Process") { mutableListOf() }.add(
                SearchResult(p.id, "Process", p.title, "${p.category} • ${p.siteTag}", "")
            )
        }

        // Reminder
        val reminders = reminderRepo.search(query)
        val fuzzyReminders = reminderRepo.getAll().first().filter { FuzzySearch.matches(query, it.message) }
        (reminders + fuzzyReminders).distinctBy { it.id }.take(5).forEach { r ->
            results.getOrPut("Reminder") { mutableListOf() }.add(
                SearchResult(r.id, "Reminder", r.message.take(60), r.category, "")
            )
        }

        // Prompt
        val prompts = promptRepo.search(query)
        val fuzzyPrompts = promptRepo.getAll().first().filter { p ->
            FuzzySearch.matches(query, p.title) || FuzzySearch.matches(query, p.category)
        }
        (prompts + fuzzyPrompts).distinctBy { it.id }.take(5).forEach { p ->
            results.getOrPut("Prompt") { mutableListOf() }.add(
                SearchResult(p.id, "Prompt", p.title, p.category, "")
            )
        }

        // Tool
        val tools = toolRepo.search(query)
        val fuzzyTools = toolRepo.getAll().first().filter { t ->
            FuzzySearch.matches(query, t.name) ||
            t.tags.split(",").any { tag -> FuzzySearch.matches(query, tag.trim()) }
        }
        (tools + fuzzyTools).distinctBy { it.id }.take(5).forEach { t ->
            results.getOrPut("Tool") { mutableListOf() }.add(
                SearchResult(t.id, "Tool", t.name, t.description.take(80), t.tags)
            )
        }

        // Note
        val notes = noteRepo.search(query)
        val fuzzyNotes = noteRepo.getAll().first().filter { n ->
            FuzzySearch.matches(query, n.title) || FuzzySearch.matches(query, n.body.take(100))
        }
        (notes + fuzzyNotes).distinctBy { it.id }.take(5).forEach { n ->
            results.getOrPut("Note") { mutableListOf() }.add(
                SearchResult(n.id, "Note", n.title, n.body.take(80), "")
            )
        }

        _counts.update { it.copy(searchResults = results) }
    }
}
