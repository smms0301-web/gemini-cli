package com.mobiapp.ui.reminder

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mobiapp.data.entity.ReminderEntity
import com.mobiapp.ui.components.*
import com.mobiapp.ui.theme.Amber
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ReminderListScreen(
    onBack: () -> Unit,
    onAddReminder: () -> Unit,
    onEditReminder: (Long) -> Unit,
    viewModel: ReminderViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val reminders by viewModel.reminders.collectAsState()
    var deleteTarget by remember { mutableStateOf<ReminderEntity?>(null) }
    val fmt = remember { SimpleDateFormat("MMM d, yyyy HH:mm", Locale.getDefault()) }

    Scaffold(
        topBar = { MobiTopBar("Reminders", onBack = onBack) },
        floatingActionButton = { MobiFab(onAddReminder) }
    ) { padding ->
        if (reminders.isEmpty()) {
            EmptyState("No reminders yet. Tap + to add one.")
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(reminders) { reminder ->
                    MobiCard {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(reminder.title, style = MaterialTheme.typography.titleMedium)
                                Text(fmt.format(Date(reminder.triggerTimeMs)), style = MaterialTheme.typography.bodySmall, color = Amber)
                                if (reminder.isRepeating) {
                                    Text("Repeating", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                            Switch(
                                checked = reminder.isEnabled,
                                onCheckedChange = { viewModel.toggleEnabled(context, reminder) },
                                colors = SwitchDefaults.colors(checkedThumbColor = Amber, checkedTrackColor = Amber.copy(alpha = 0.4f))
                            )
                            IconButton(onClick = { onEditReminder(reminder.id) }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Amber)
                            }
                            IconButton(onClick = { deleteTarget = reminder }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }

    deleteTarget?.let { reminder ->
        DeleteDialog(
            message = "Delete \"${reminder.title}\"?",
            onConfirm = { viewModel.deleteReminder(context, reminder); deleteTarget = null },
            onDismiss = { deleteTarget = null }
        )
    }
}
