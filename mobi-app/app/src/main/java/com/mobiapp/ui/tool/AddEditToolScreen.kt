package com.mobiapp.ui.tool

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mobiapp.data.entity.ToolEntity
import com.mobiapp.ui.components.MobiTopBar
import com.mobiapp.ui.theme.Amber

@Composable
fun AddEditToolScreen(
    toolId: Long?,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: ToolViewModel = hiltViewModel()
) {
    val existing by viewModel.getTool(toolId ?: -1L).collectAsState(initial = null)
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf("") }
    var initialized by remember { mutableStateOf(false) }

    LaunchedEffect(existing) {
        if (!initialized && toolId != null && existing != null) {
            name = existing!!.name
            description = existing!!.description
            url = existing!!.url
            category = existing!!.category
            tags = existing!!.tags
            initialized = true
        }
    }

    Scaffold(
        topBar = { MobiTopBar(if (toolId == null) "New Tool" else "Edit Tool", onBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Amber))
            OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category") }, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Amber))
            OutlinedTextField(value = tags, onValueChange = { tags = it }, label = { Text("Tags (comma separated)") }, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Amber))
            OutlinedTextField(value = url, onValueChange = { url = it }, label = { Text("URL") }, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Amber))
            OutlinedTextField(
                value = description, onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                maxLines = 5,
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Amber)
            )
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val entity = ToolEntity(
                            id = toolId ?: 0L,
                            name = name.trim(),
                            description = description.trim(),
                            url = url.trim(),
                            category = category.trim(),
                            tags = tags.trim(),
                            isFavorite = existing?.isFavorite ?: false
                        )
                        viewModel.saveTool(entity) { onSaved() }
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
