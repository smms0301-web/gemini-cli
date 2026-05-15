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
import com.mobiapp.ui.components.*
import com.mobiapp.ui.process.*
import com.mobiapp.ui.theme.*

private val CATEGORIES = listOf("Report Writing", "Email", "Translation", "Training Content", "Analysis", "CCTV", "General", "Code", "Research")

@Composable
fun AddEditPromptScreen(
    promptId: Long?,
    onBack: () -> Unit,
    viewModel: PromptViewModel = hiltViewModel()
) {
    var title by remember { mutableStateOf("") }
    var promptText by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(CATEGORIES.last()) }
    var note by remember { mutableStateOf("") }
    var isEdit by remember { mutableStateOf(false) }

    LaunchedEffect(promptId) {
        promptId?.let { id ->
            val p = viewModel.getById(id) ?: return@let
            title = p.title; promptText = p.promptText; category = p.category; note = p.personalNote; isEdit = true
        }
    }

    Scaffold(
        containerColor = androidx.compose.ui.graphics.Color(0xFF0F0F0F),
        topBar = { MobiTopBar(if (isEdit) "Edit Prompt" else "New Prompt", onBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding)
                .verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MobiTextField(value = title, onValueChange = { title = it }, label = "Prompt Title")
            MobiTextField(value = promptText, onValueChange = { promptText = it }, label = "Prompt Text", minLines = 6)
            MobiTextField(value = note, onValueChange = { note = it }, label = "Personal Note (optional)", minLines = 2)
            Text("Category", color = OnSurfaceVariant, style = MaterialTheme.typography.labelMedium)
            ChipSelector(CATEGORIES, category) { category = it }
            Button(
                onClick = {
                    if (title.isBlank() || promptText.isBlank()) return@Button
                    viewModel.save(
                        PromptEntity(id = promptId ?: 0L, title = title.trim(), promptText = promptText.trim(), category = category, personalNote = note.trim()),
                        onBack
                    )
                },
                enabled = title.isNotBlank() && promptText.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Amber, contentColor = OnAmber)
            ) { Text(if (isEdit) "Save Changes" else "Save Prompt") }
        }
    }
}
