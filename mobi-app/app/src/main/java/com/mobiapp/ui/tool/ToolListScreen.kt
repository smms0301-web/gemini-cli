package com.mobiapp.ui.tool

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mobiapp.data.entity.ToolEntity
import com.mobiapp.ui.components.*
import com.mobiapp.ui.theme.Amber

@Composable
fun ToolListScreen(
    onBack: () -> Unit,
    onAddTool: () -> Unit,
    onToolClick: (Long) -> Unit,
    viewModel: ToolViewModel = hiltViewModel()
) {
    val query by viewModel.query.collectAsState()
    val tools by viewModel.tools.collectAsState()
    var deleteTarget by remember { mutableStateOf<ToolEntity?>(null) }

    Scaffold(
        topBar = { MobiTopBar("AI Tools Directory", onBack = onBack) },
        floatingActionButton = { MobiFab(onAddTool) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            MobiSearchBar(
                query = query,
                onQueryChange = { viewModel.query.value = it },
                placeholder = "Search by name, tag, category..."
            )
            Spacer(Modifier.height(12.dp))
            if (tools.isEmpty()) {
                EmptyState("No tools yet. Tap + to add one.")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(tools) { tool ->
                        MobiCard(onClick = { onToolClick(tool.id) }) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column(Modifier.weight(1f)) {
                                    Text(tool.name, style = MaterialTheme.typography.titleMedium)
                                    if (tool.category.isNotBlank()) {
                                        Text(tool.category, style = MaterialTheme.typography.bodySmall, color = Amber)
                                    }
                                    if (tool.description.isNotBlank()) {
                                        Text(tool.description, style = MaterialTheme.typography.bodySmall, maxLines = 2)
                                    }
                                    if (tool.tags.isNotBlank()) {
                                        Row(modifier = Modifier.padding(top = 4.dp)) {
                                            tool.tags.split(",").take(4).forEach { TagChip(it.trim()) }
                                        }
                                    }
                                }
                                Column {
                                    IconButton(onClick = { viewModel.toggleFavorite(tool) }) {
                                        Icon(
                                            if (tool.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                            contentDescription = "Favorite",
                                            tint = Amber
                                        )
                                    }
                                    IconButton(onClick = { deleteTarget = tool }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    deleteTarget?.let { tool ->
        DeleteDialog(
            message = "Delete \"${tool.name}\"?",
            onConfirm = { viewModel.deleteTool(tool); deleteTarget = null },
            onDismiss = { deleteTarget = null }
        )
    }
}
