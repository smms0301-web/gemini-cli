package com.mobiapp.ui.reminder

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mobiapp.data.entity.ReminderEntity
import com.mobiapp.ui.components.MobiTopBar
import com.mobiapp.ui.theme.Amber
import java.util.*

@Composable
fun AddEditReminderScreen(
    reminderId: Long?,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: ReminderViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val existing by viewModel.getReminder(reminderId ?: -1L).collectAsState(initial = null)
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isRepeating by remember { mutableStateOf(false) }
    var hour by remember { mutableStateOf(9) }
    var minute by remember { mutableStateOf(0) }
    var initialized by remember { mutableStateOf(false) }

    LaunchedEffect(existing) {
        if (!initialized && reminderId != null && existing != null) {
            title = existing!!.title
            description = existing!!.description
            isRepeating = existing!!.isRepeating
            val cal = Calendar.getInstance().apply { timeInMillis = existing!!.triggerTimeMs }
            hour = cal.get(Calendar.HOUR_OF_DAY)
            minute = cal.get(Calendar.MINUTE)
            initialized = true
        }
    }

    Scaffold(
        topBar = { MobiTopBar(if (reminderId == null) "New Reminder" else "Edit Reminder", onBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = title, onValueChange = { title = it },
                label = { Text("Title") }, modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Amber)
            )
            OutlinedTextField(
                value = description, onValueChange = { description = it },
                label = { Text("Note (optional)") }, modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Amber)
            )

            Text("Time", style = MaterialTheme.typography.titleSmall)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = hour.toString().padStart(2, '0'),
                    onValueChange = { it.toIntOrNull()?.coerceIn(0, 23)?.let { h -> hour = h } },
                    label = { Text("HH") }, modifier = Modifier.width(80.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Amber)
                )
                Text(":", style = MaterialTheme.typography.headlineMedium)
                OutlinedTextField(
                    value = minute.toString().padStart(2, '0'),
                    onValueChange = { it.toIntOrNull()?.coerceIn(0, 59)?.let { m -> minute = m } },
                    label = { Text("MM") }, modifier = Modifier.width(80.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Amber)
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Repeat daily", style = MaterialTheme.typography.bodyLarge)
                Switch(
                    checked = isRepeating, onCheckedChange = { isRepeating = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = Amber, checkedTrackColor = Amber.copy(alpha = 0.4f))
                )
            }

            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val cal = Calendar.getInstance().apply {
                            set(Calendar.HOUR_OF_DAY, hour)
                            set(Calendar.MINUTE, minute)
                            set(Calendar.SECOND, 0)
                            if (timeInMillis <= System.currentTimeMillis()) add(Calendar.DAY_OF_YEAR, 1)
                        }
                        val entity = ReminderEntity(
                            id = reminderId ?: 0L,
                            title = title.trim(),
                            description = description.trim(),
                            triggerTimeMs = cal.timeInMillis,
                            isRepeating = isRepeating,
                            repeatIntervalMs = if (isRepeating) 86_400_000L else 0L,
                            isEnabled = true
                        )
                        viewModel.saveReminder(context, entity) { onSaved() }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Amber)
            ) {
                Text("Save", color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}
