package com.mobiapp.ui.process

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mobiapp.data.entity.ProcessStepEntity
import com.mobiapp.ui.components.*
import com.mobiapp.ui.theme.Amber

@Composable
fun ProcessDetailScreen(
    processId: Long,
    onBack: () -> Unit,
    onEdit: (Long) -> Unit,
    onAddStep: (Long) -> Unit,
    onStepClick: (Long, Long) -> Unit,
    onExportPdf: (Long) -> Unit,
    viewModel: ProcessViewModel = hiltViewModel()
) {
    val process by viewModel.getProcess(processId).collectAsState(initial = null)
    val steps by viewModel.getSteps(processId).collectAsState(initial = emptyList())
    var deleteTarget by remember { mutableStateOf<ProcessStepEntity?>(null) }

    Scaffold(
        topBar = {
            MobiTopBar(
                title = process?.title ?: "Process",
                onBack = onBack,
                actions = {
                    IconButton(onClick = { onExportPdf(processId) }) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = "Export PDF", tint = Amber)
                    }
                    IconButton(onClick = { onEdit(processId) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Amber)
                    }
                }
            )
        },
        floatingActionButton = { MobiFab { onAddStep(processId) } }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            process?.let { p ->
                item {
                    if (p.description.isNotBlank()) {
                        Text(p.description, style = MaterialTheme.typography.bodyLarge)
                        Spacer(Modifier.height(4.dp))
                    }
                    if (p.category.isNotBlank()) {
                        TagChip(p.category)
                    }
                    Spacer(Modifier.height(8.dp))
                    Text("Steps (${steps.size})", style = MaterialTheme.typography.titleMedium, color = Amber)
                }
            }
            if (steps.isEmpty()) {
                item { EmptyState("No steps yet. Tap + to add.") }
            } else {
                items(steps) { step ->
                    MobiCard(onClick = { onStepClick(processId, step.id) }) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column(Modifier.weight(1f)) {
                                Text("${step.stepNumber}. ${step.title}", style = MaterialTheme.typography.titleMedium)
                                if (step.description.isNotBlank()) {
                                    Text(step.description, style = MaterialTheme.typography.bodySmall, maxLines = 2)
                                }
                            }
                            IconButton(onClick = { deleteTarget = step }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }

    deleteTarget?.let { step ->
        DeleteDialog(
            message = "Delete step \"${step.title}\"?",
            onConfirm = { viewModel.deleteStep(step); deleteTarget = null },
            onDismiss = { deleteTarget = null }
        )
    }
}
