package com.mobiapp.ui.process

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mobiapp.data.entity.ProcessEntity
import com.mobiapp.data.entity.ProcessStepEntity
import com.mobiapp.navigation.Screen
import com.mobiapp.ui.components.*
import com.mobiapp.ui.theme.*

@Composable
fun ProcessDetailScreen(
    processId: Long,
    onNavigate: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: ProcessViewModel = hiltViewModel()
) {
    var process by remember { mutableStateOf<ProcessEntity?>(null) }
    val steps by viewModel.getSteps(processId).collectAsStateWithLifecycle()
    var showDeleteStep by remember { mutableStateOf<ProcessStepEntity?>(null) }

    LaunchedEffect(processId) { process = viewModel.getProcess(processId) }

    Scaffold(
        containerColor = Background,
        topBar = {
            MobiTopBar(
                title = process?.title ?: "Process",
                onBack = onBack,
                actions = {
                    IconButton(onClick = { onNavigate(Screen.AddEditProcess.createRoute(processId)) }) {
                        Icon(Icons.Default.Edit, "Edit", tint = Amber)
                    }
                    IconButton(onClick = { onNavigate(Screen.PdfExport.createRoute(processId)) }) {
                        Icon(Icons.Default.PictureAsPdf, "Export PDF", tint = OnSurfaceVariant)
                    }
                }
            )
        },
        floatingActionButton = {
            MobiFab(onClick = { onNavigate(Screen.AddEditStep.createRoute(processId)) })
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                process?.let { p ->
                    MobiCard {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            TagChip(p.category); TagChip(p.siteTag, color = AmberLight)
                        }
                        if (p.description.isNotBlank()) {
                            Spacer(Modifier.height(8.dp))
                            Text(p.description, color = OnSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
                        }
                        Spacer(Modifier.height(8.dp))
                        val done = steps.count { it.isDone }
                        LinearProgressIndicator(
                            progress = { if (steps.isEmpty()) 0f else done.toFloat() / steps.size },
                            modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                            color = Amber,
                            trackColor = SurfaceContainer
                        )
                        Text("$done / ${steps.size} steps done", color = OnSurfaceVariant, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
            if (steps.isEmpty()) {
                item { EmptyState("No steps yet. Tap + to add the first step.", Icons.Default.ListAlt) }
            } else {
                items(steps, key = { it.id }) { step ->
                    StepCard(
                        step = step,
                        onClick = { onNavigate(Screen.StepDetail.createRoute(step.id, processId)) },
                        onToggleDone = { viewModel.toggleStepDone(step.id, !step.isDone) },
                        onDelete = { showDeleteStep = step }
                    )
                }
            }
            item { Spacer(Modifier.height(80.dp)) }
        }
    }

    showDeleteStep?.let { step ->
        DeleteDialog(
            title = "Delete step \"${step.title}\"?",
            onConfirm = { viewModel.deleteStep(step); showDeleteStep = null },
            onDismiss = { showDeleteStep = null }
        )
    }
}

@Composable
fun StepCard(
    step: ProcessStepEntity,
    onClick: () -> Unit,
    onToggleDone: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (step.isDone) SurfaceContainer else SurfaceVariant)
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = step.isDone,
            onCheckedChange = { onToggleDone() },
            colors = CheckboxDefaults.colors(
                checkedColor = Amber, uncheckedColor = OnSurfaceVariant, checkmarkColor = OnAmber
            )
        )
        Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "Step ${step.stepNumber}",
                    color = Amber,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
                if (step.voiceNotePath.isNotBlank()) Icon(Icons.Default.Mic, null, tint = OnSurfaceVariant, modifier = Modifier.size(14.dp))
                val imgCount = step.imagePaths.split(",").count { it.isNotBlank() }
                if (imgCount > 0) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        Icon(Icons.Default.Image, null, tint = OnSurfaceVariant, modifier = Modifier.size(14.dp))
                        Text("$imgCount", color = OnSurfaceVariant, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
            Text(step.title, color = if (step.isDone) OnSurfaceVariant else OnBackground,
                style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            if (step.note.isNotBlank()) {
                Text(step.note.take(80) + if (step.note.length > 80) "…" else "",
                    color = OnSurfaceVariant, style = MaterialTheme.typography.bodySmall, maxLines = 2)
            }
        }
        IconButton(onClick = onDelete) {
            Icon(Icons.Default.DeleteOutline, "Delete", tint = OnSurfaceVariant)
        }
    }
}
