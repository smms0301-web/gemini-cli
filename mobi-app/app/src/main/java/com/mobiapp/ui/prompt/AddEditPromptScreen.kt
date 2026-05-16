package com.mobiapp.ui.prompt

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mobiapp.data.entity.PromptEntity
import com.mobiapp.ui.components.MobiTopBar
import com.mobiapp.ui.theme.Amber

@Composable
fun AddEditPromptScreen(
    promptId: Long?,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: PromptViewModel = hiltViewModel()
) {
    val existing by viewModel.getPrompt(promptId ?: -1L).collectAsState(initial = null)
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf("") }
    var initialized by remember { mutableStateOf(false) }

    LaunchedEffect(existing) {
        if (!initialized && promptId != null && existing != null) {
            title = existing!!.title
            content = existing!!.content
            category = existing!!.category
            tags = existing!!.tags
            initialized = true
        }
    }

    Scaffold(
        topBar = { MobiTopBar(if (promptId == null) "New Prompt" else "Edit Prompt", onBack = onBack) }
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
                value = tags, onValueChange = { tags = it },
                label = { Text("Tags (comma separated)") }, modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Amber)
            )
            OutlinedTextField(
                value = content, onValueChange = { content = it },
                label = { Text("Prompt Text") },
                modifier = Modifier.fillMaxWidth().height(200.dp),
                maxLines = 12,
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Amber)
            )
            Button(
                onClick = {
                    if (title.isNotBlank() && content.isNotBlank()) {
                        val entity = PromptEntity(
                            id = promptId ?: 0L,
                            title = title.trim(),
                            content = content.trim(),
                            category = category.trim(),
                            tags = tags.trim(),
                            isFavorite = existing?.isFavorite ?: false
                        )
                        viewModel.savePrompt(entity) { onSaved() }
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
