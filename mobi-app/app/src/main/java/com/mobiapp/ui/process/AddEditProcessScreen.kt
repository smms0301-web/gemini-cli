package com.mobiapp.ui.process

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mobiapp.data.entity.ProcessEntity
import com.mobiapp.ui.components.MobiTopBar
import com.mobiapp.ui.theme.Amber

@Composable
fun AddEditProcessScreen(
    processId: Long?,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: ProcessViewModel = hiltViewModel()
) {
    val existing by viewModel.getProcess(processId ?: -1L).collectAsState(initial = null)
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var initialized by remember { mutableStateOf(false) }

    LaunchedEffect(existing) {
        if (!initialized && processId != null && existing != null) {
            title = existing!!.title
            description = existing!!.description
            category = existing!!.category
            initialized = true
        }
    }

    Scaffold(
        topBar = { MobiTopBar(if (processId == null) "New Process" else "Edit Process", onBack = onBack) }
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
                value = category, onValueChange = { category = it },
                label = { Text("Category") }, modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Amber)
            )
            OutlinedTextField(
                value = description, onValueChange = { description = it },
                label = { Text("Description") }, modifier = Modifier.fillMaxWidth().height(120.dp),
                maxLines = 5,
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Amber)
            )
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val entity = ProcessEntity(
                            id = processId ?: 0L,
                            title = title.trim(),
                            description = description.trim(),
                            category = category.trim()
                        )
                        viewModel.saveProcess(entity) { onSaved() }
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
