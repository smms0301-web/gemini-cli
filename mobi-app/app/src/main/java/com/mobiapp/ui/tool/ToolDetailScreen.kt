package com.mobiapp.ui.tool

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
import com.mobiapp.ui.components.TagChip
import com.mobiapp.ui.theme.Amber

@Composable
fun ToolDetailScreen(
    toolId: Long,
    onBack: () -> Unit,
    onEdit: (Long) -> Unit,
    viewModel: ToolViewModel = hiltViewModel()
) {
    val tool by viewModel.getTool(toolId).collectAsState(initial = null)

    Scaffold(
        topBar = {
            MobiTopBar(
                title = tool?.name ?: "Tool",
                onBack = onBack,
                actions = {
                    IconButton(onClick = { onEdit(toolId) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Amber)
                    }
                }
            )
        }
    ) { padding ->
        tool?.let { t ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (t.category.isNotBlank()) TagChip(t.category)
                if (t.tags.isNotBlank()) {
                    Row { t.tags.split(",").forEach { TagChip(it.trim()) } }
                }
                if (t.description.isNotBlank()) {
                    Text(t.description, style = MaterialTheme.typography.bodyLarge)
                }
                if (t.url.isNotBlank()) {
                    Divider()
                    Text("URL", style = MaterialTheme.typography.labelMedium, color = Amber)
                    Text(t.url, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
