package com.mobiapp.ui.prompt

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mobiapp.ui.components.MobiTopBar
import com.mobiapp.ui.components.TagChip
import com.mobiapp.ui.theme.Amber
import kotlinx.coroutines.launch

@Composable
fun PromptDetailScreen(
    promptId: Long,
    onBack: () -> Unit,
    onEdit: (Long) -> Unit,
    viewModel: PromptViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val prompt by viewModel.getPrompt(promptId).collectAsState(initial = null)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            MobiTopBar(
                title = prompt?.title ?: "Prompt",
                onBack = onBack,
                actions = {
                    IconButton(onClick = {
                        prompt?.let {
                            val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            cm.setPrimaryClip(ClipData.newPlainText("prompt", it.content))
                            scope.launch { snackbarHostState.showSnackbar("Copied to clipboard") }
                        }
                    }) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = Amber)
                    }
                    IconButton(onClick = { onEdit(promptId) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Amber)
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        prompt?.let { p ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (p.category.isNotBlank()) {
                    TagChip(p.category)
                }
                if (p.tags.isNotBlank()) {
                    Row { p.tags.split(",").forEach { TagChip(it.trim()) } }
                }
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Text(
                        p.content,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}
