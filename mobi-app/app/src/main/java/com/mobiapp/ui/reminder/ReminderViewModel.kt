package com.mobiapp.ui.reminder

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobiapp.data.entity.ReminderEntity
import com.mobiapp.repository.ReminderRepository
import com.mobiapp.service.ReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReminderViewModel @Inject constructor(
    private val repo: ReminderRepository
) : ViewModel() {

    val reminders: StateFlow<List<ReminderEntity>> = repo.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    suspend fun getById(id: Long) = repo.getById(id)

    fun save(context: Context, reminder: ReminderEntity) = viewModelScope.launch {
        val id = if (reminder.id == 0L) repo.insert(reminder) else { repo.update(reminder); reminder.id }
        val saved = repo.getById(id) ?: return@launch
        if (saved.isEnabled) ReminderScheduler.schedule(context, saved)
        else ReminderScheduler.cancel(context, id)
    }

    fun delete(context: Context, reminder: ReminderEntity) = viewModelScope.launch {
        ReminderScheduler.cancel(context, reminder.id)
        repo.delete(reminder)
    }

    fun toggleEnabled(context: Context, reminder: ReminderEntity) = viewModelScope.launch {
        val enabled = !reminder.isEnabled
        repo.setEnabled(reminder.id, enabled)
        if (enabled) ReminderScheduler.schedule(context, reminder.copy(isEnabled = true))
        else ReminderScheduler.cancel(context, reminder.id)
    }
}
