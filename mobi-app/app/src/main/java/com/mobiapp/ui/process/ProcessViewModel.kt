package com.mobiapp.ui.process

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobiapp.data.entity.ProcessEntity
import com.mobiapp.data.entity.ProcessStepEntity
import com.mobiapp.repository.ProcessRepository
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

    val processes = repo.getAllProcesses().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getProcess(id: Long) = repo.getProcessById(id)
    fun getSteps(processId: Long) = repo.getStepsForProcess(processId)
    fun getStep(id: Long) = repo.getStepById(id)

    fun saveProcess(process: ProcessEntity, onDone: (Long) -> Unit) = viewModelScope.launch {
        val id = if (process.id == 0L) repo.insertProcess(process)
        else { repo.updateProcess(process.copy(updatedAt = System.currentTimeMillis())); process.id }
        onDone(id)
    }

    fun deleteProcess(process: ProcessEntity) = viewModelScope.launch { repo.deleteProcess(process) }

    fun saveStep(step: ProcessStepEntity, onDone: () -> Unit) = viewModelScope.launch {
        val num = if (step.id == 0L) (repo.getMaxStepNumber(step.processId) ?: 0) + 1 else step.stepNumber
        if (step.id == 0L) repo.insertStep(step.copy(stepNumber = num))
        else repo.updateStep(step)
        onDone()
    }

    fun deleteStep(step: ProcessStepEntity) = viewModelScope.launch { repo.deleteStep(step) }

    fun exportPdf(context: Context, processId: Long, onResult: (File?) -> Unit) = viewModelScope.launch {
        repo.getProcessById(processId).firstOrNull()?.let { process ->
            repo.getStepsForProcess(processId).firstOrNull()?.let { steps ->
                val file = PdfExporter.export(context, process, steps)
                onResult(file)
            } ?: onResult(null)
        } ?: onResult(null)
    }
}
