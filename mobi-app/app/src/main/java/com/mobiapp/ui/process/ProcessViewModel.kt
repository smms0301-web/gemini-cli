package com.mobiapp.ui.process

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobiapp.data.entity.ProcessEntity
import com.mobiapp.data.entity.ProcessStepEntity
import com.mobiapp.repository.ProcessRepository
import com.mobiapp.util.FuzzySearch
import com.mobiapp.util.PdfExportOptions
import com.mobiapp.util.PdfExporter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ProcessViewModel @Inject constructor(
    private val repo: ProcessRepository
) : ViewModel() {

    val processes: StateFlow<List<ProcessEntity>> = repo.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val filteredProcesses: StateFlow<List<ProcessEntity>> = combine(processes, _searchQuery) { list, q ->
        if (q.isBlank()) list
        else list.filter { p ->
            FuzzySearch.matches(q, p.title) || FuzzySearch.matches(q, p.category) || FuzzySearch.matches(q, p.siteTag)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSearchQuery(q: String) { _searchQuery.value = q }

    fun getSteps(processId: Long): StateFlow<List<ProcessStepEntity>> =
        repo.getSteps(processId).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    suspend fun getProcess(id: Long) = repo.getById(id)
    suspend fun getStep(id: Long) = repo.getStepById(id)

    fun saveProcess(process: ProcessEntity, onDone: (Long) -> Unit) = viewModelScope.launch {
        val id = if (process.id == 0L) repo.insert(process)
        else { repo.update(process.copy(updatedAt = System.currentTimeMillis())); process.id }
        onDone(id)
    }

    fun deleteProcess(process: ProcessEntity) = viewModelScope.launch { repo.delete(process) }

    fun saveStep(step: ProcessStepEntity, onDone: (Long) -> Unit) = viewModelScope.launch {
        val id = if (step.id == 0L) {
            val num = repo.getNextStepNumber(step.processId)
            repo.insertStep(step.copy(stepNumber = num))
        } else {
            repo.updateStep(step.copy(updatedAt = System.currentTimeMillis()))
            step.id
        }
        onDone(id)
    }

    fun deleteStep(step: ProcessStepEntity) = viewModelScope.launch { repo.deleteStep(step) }

    fun toggleStepDone(id: Long, isDone: Boolean) = viewModelScope.launch {
        repo.toggleStepDone(id, isDone)
    }

    fun exportPdf(
        context: Context,
        processId: Long,
        options: PdfExportOptions,
        onResult: (File?) -> Unit
    ) = viewModelScope.launch {
        val process = repo.getById(processId) ?: return@launch onResult(null)
        val steps = repo.getStepsSync(processId)
        val file = PdfExporter.export(context, process, steps, options)
        onResult(file)
    }
}
