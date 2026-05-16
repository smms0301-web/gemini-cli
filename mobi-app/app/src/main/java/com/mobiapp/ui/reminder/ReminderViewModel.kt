package com.mobiapp.ui.reminder

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobiapp.data.entity.ReminderEntity
import com.mobiapp.repository.ReminderRepository
import com.mobiapp.service.ReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReminderViewModel @Inject constructor(
    private val repo: ReminderRepository
) : ViewModel() {

    val reminders = repo.getAllReminders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getReminder(id: Long) = repo.getReminderById(id)

    fun saveReminder(context: Context, reminder: ReminderEntity, onDone: () -> Unit) =
        viewModelScope.launch {
            val id = if (reminder.id == 0L) repo.insertReminder(reminder)
            else { repo.updateReminder(reminder); reminder.id }
            val saved = reminder.copy(id = id)
            if (saved.isEnabled) ReminderScheduler.schedule(context, saved)
            onDone()
        }

    fun toggleEnabled(context: Context, reminder: ReminderEntity) = viewModelScope.launch {
        val updated = reminder.copy(isEnabled = !reminder.isEnabled)
        repo.updateReminder(updated)
        if (updated.isEnabled) ReminderScheduler.schedule(context, updated)
        else ReminderScheduler.cancel(context, updated.id)
    }

    fun deleteReminder(context: Context, reminder: ReminderEntity) = viewModelScope.launch {
        ReminderScheduler.cancel(context, reminder.id)
        repo.deleteReminder(reminder)
    }
}
