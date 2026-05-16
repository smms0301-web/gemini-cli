package com.mobiapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobiapp.repository.*
import com.mobiapp.util.FuzzySearch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class SearchResult(val module: String, val id: Long, val title: String, val subtitle: String)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val processRepo: ProcessRepository,
    private val reminderRepo: ReminderRepository,
    private val promptRepo: PromptRepository,
    private val toolRepo: ToolRepository,
    private val noteRepo: NoteRepository
) : ViewModel() {

    val query = MutableStateFlow("")

    val results: StateFlow<Map<String, List<SearchResult>>> = query
        .debounce(300)
        .combine(
            combine(
                processRepo.getAllProcesses(),
                reminderRepo.getAllReminders(),
                promptRepo.getAllPrompts(),
                toolRepo.getAllTools(),
                noteRepo.getAllNotes()
            ) { processes, reminders, prompts, tools, notes ->
                Quintuple(processes, reminders, prompts, tools, notes)
            }
        ) { q, data ->
            if (q.isBlank()) return@combine emptyMap()
            val map = mutableMapOf<String, MutableList<SearchResult>>()

            data.first.filter { FuzzySearch.matches(q, it.title + " " + it.description) }.forEach {
                map.getOrPut("Processes") { mutableListOf() }
                    .add(SearchResult("Processes", it.id, it.title, it.category))
            }
            data.second.filter { FuzzySearch.matches(q, it.title + " " + it.description) }.forEach {
                map.getOrPut("Reminders") { mutableListOf() }
                    .add(SearchResult("Reminders", it.id, it.title, it.description))
            }
            data.third.filter { FuzzySearch.matches(q, it.title + " " + it.content + " " + it.tags) }.forEach {
                map.getOrPut("Prompts") { mutableListOf() }
                    .add(SearchResult("Prompts", it.id, it.title, it.category))
            }
            data.fourth.filter { FuzzySearch.matches(q, it.name + " " + it.description + " " + it.tags) }.forEach {
                map.getOrPut("Tools") { mutableListOf() }
                    .add(SearchResult("Tools", it.id, it.name, it.category))
            }
            data.fifth.filter { FuzzySearch.matches(q, it.title + " " + it.content) }.forEach {
                map.getOrPut("Notes") { mutableListOf() }
                    .add(SearchResult("Notes", it.id, it.title, it.content.take(60)))
            }
            map
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    fun onQueryChange(q: String) { query.value = q }
}

private data class Quintuple<A, B, C, D, E>(val first: A, val second: B, val third: C, val fourth: D, val fifth: E)
