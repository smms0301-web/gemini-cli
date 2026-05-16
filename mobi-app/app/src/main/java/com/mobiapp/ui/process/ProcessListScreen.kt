package com.mobiapp.ui.process

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mobiapp.data.entity.ProcessEntity
import com.mobiapp.ui.components.*
import com.mobiapp.ui.theme.Amber

@Composable
fun ProcessListScreen(
    onBack: () -> Unit,
    onAddProcess: () -> Unit,
    onProcessClick: (Long) -> Unit,
    viewModel: ProcessViewModel = hiltViewModel()
) {
    val processes by viewModel.processes.collectAsState()
    var deleteTarget by remember { mutableStateOf<ProcessEntity?>(null) }

    Scaffold(
        topBar = { MobiTopBar("Process Tracker", onBack = onBack) },
        floatingActionButton = { MobiFab(onAddProcess) }
    ) { padding ->
        if (processes.isEmpty()) {
            EmptyState("No processes yet. Tap + to add one.")
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(processes) { process ->
                    MobiCard(onClick = { onProcessClick(process.id) }) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column(Modifier.weight(1f)) {
                                Text(process.title, style = MaterialTheme.typography.titleMedium)
                                if (process.category.isNotBlank()) {
                                    Text(process.category, style = MaterialTheme.typography.bodySmall, color = Amber)
                                }
                                if (process.description.isNotBlank()) {
                                    Text(
                                        process.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        maxLines = 2
                                    )
                                }
                            }
                            IconButton(onClick = { deleteTarget = process }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }

    deleteTarget?.let { process ->
        DeleteDialog(
            message = "Delete \"${process.title}\"?",
            onConfirm = { viewModel.deleteProcess(process); deleteTarget = null },
            onDismiss = { deleteTarget = null }
        )
    }
}
