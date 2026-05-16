package com.mobiapp.ui.prompt

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
import com.mobiapp.data.entity.PromptEntity
import com.mobiapp.ui.components.*
import com.mobiapp.ui.theme.Amber

@Composable
fun PromptListScreen(
    onBack: () -> Unit,
    onAddPrompt: () -> Unit,
    onPromptClick: (Long) -> Unit,
    viewModel: PromptViewModel = hiltViewModel()
) {
    val query by viewModel.query.collectAsState()
    val prompts by viewModel.prompts.collectAsState()
    var deleteTarget by remember { mutableStateOf<PromptEntity?>(null) }

    Scaffold(
        topBar = { MobiTopBar("AI Prompts Library", onBack = onBack) },
        floatingActionButton = { MobiFab(onAddPrompt) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            MobiSearchBar(query = query, onQueryChange = { viewModel.query.value = it })
            Spacer(Modifier.height(12.dp))
            if (prompts.isEmpty()) {
                EmptyState("No prompts yet. Tap + to add one.")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(prompts) { prompt ->
                        MobiCard(onClick = { onPromptClick(prompt.id) }) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column(Modifier.weight(1f)) {
                                    Text(prompt.title, style = MaterialTheme.typography.titleMedium)
                                    if (prompt.category.isNotBlank()) {
                                        Text(prompt.category, style = MaterialTheme.typography.bodySmall, color = Amber)
                                    }
                                    Text(prompt.content, style = MaterialTheme.typography.bodySmall, maxLines = 2)
                                    if (prompt.tags.isNotBlank()) {
                                        Row(modifier = Modifier.padding(top = 4.dp)) {
                                            prompt.tags.split(",").take(3).forEach { TagChip(it.trim()) }
                                        }
                                    }
                                }
                                Column {
                                    IconButton(onClick = { viewModel.toggleFavorite(prompt) }) {
                                        Icon(
                                            if (prompt.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                            contentDescription = "Favorite",
                                            tint = Amber
                                        )
                                    }
                                    IconButton(onClick = { deleteTarget = prompt }) {
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

    deleteTarget?.let { prompt ->
        DeleteDialog(
            message = "Delete \"${prompt.title}\"?",
            onConfirm = { viewModel.deletePrompt(prompt); deleteTarget = null },
            onDismiss = { deleteTarget = null }
        )
    }
}
