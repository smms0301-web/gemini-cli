package com.mobiapp.ui.process

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mobiapp.ui.components.MobiTopBar
import com.mobiapp.ui.theme.Amber

@Composable
fun StepDetailScreen(
    processId: Long,
    stepId: Long,
    onBack: () -> Unit,
    onEdit: (Long, Long) -> Unit,
    viewModel: ProcessViewModel = hiltViewModel()
) {
    val step by viewModel.getStep(stepId).collectAsState(initial = null)

    Scaffold(
        topBar = {
            MobiTopBar(
                title = step?.let { "Step ${it.stepNumber}" } ?: "Step",
                onBack = onBack,
                actions = {
                    IconButton(onClick = { onEdit(processId, stepId) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Amber)
                    }
                }
            )
        }
    ) { padding ->
        step?.let { s ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(s.title, style = MaterialTheme.typography.headlineSmall)
                if (s.description.isNotBlank()) {
                    Text(s.description, style = MaterialTheme.typography.bodyLarge)
                }
                if (s.voiceNotePath != null) {
                    Text("Voice note: ${s.voiceNotePath}", style = MaterialTheme.typography.bodySmall, color = Amber)
                }
            }
        }
    }
}
