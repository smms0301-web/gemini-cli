package com.mobiapp.ui.process

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mobiapp.data.entity.ProcessStepEntity
import com.mobiapp.ui.components.MobiTopBar
import com.mobiapp.ui.theme.Amber

@Composable
fun AddEditStepScreen(
    processId: Long,
    stepId: Long?,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: ProcessViewModel = hiltViewModel()
) {
    val existing by viewModel.getStep(stepId ?: -1L).collectAsState(initial = null)
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var initialized by remember { mutableStateOf(false) }

    LaunchedEffect(existing) {
        if (!initialized && stepId != null && existing != null) {
            title = existing!!.title
            description = existing!!.description
            initialized = true
        }
    }

    Scaffold(
        topBar = { MobiTopBar(if (stepId == null) "New Step" else "Edit Step", onBack = onBack) }
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
                label = { Text("Step Title") }, modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Amber)
            )
            OutlinedTextField(
                value = description, onValueChange = { description = it },
                label = { Text("Description / Instructions") },
                modifier = Modifier.fillMaxWidth().height(160.dp),
                maxLines = 8,
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Amber)
            )
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val entity = ProcessStepEntity(
                            id = stepId ?: 0L,
                            processId = processId,
                            stepNumber = existing?.stepNumber ?: 0,
                            title = title.trim(),
                            description = description.trim(),
                            voiceNotePath = existing?.voiceNotePath,
                            imagePaths = existing?.imagePaths ?: ""
                        )
                        viewModel.saveStep(entity) { onSaved() }
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
