package com.mobiapp.ui.prompt

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mobiapp.data.entity.PromptEntity
import com.mobiapp.navigation.Screen
import com.mobiapp.ui.components.*
import com.mobiapp.ui.theme.*

@Composable
fun PromptListScreen(
    onNavigate: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: PromptViewModel = hiltViewModel()
) {
    val prompts by viewModel.filtered.collectAsStateWithLifecycle()
    val query by viewModel.query.collectAsStateWithLifecycle()
    var showDelete by remember { mutableStateOf<PromptEntity?>(null) }

    Scaffold(
        containerColor = Background,
        topBar = { MobiTopBar("AI Prompts Library", onBack = onBack) },
        floatingActionButton = { MobiFab(onClick = { onNavigate(Screen.AddEditPrompt.createRoute()) }) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            MobiSearchBar(query, viewModel::setQuery, "Search prompts…", Modifier.padding(16.dp))
            if (prompts.isEmpty()) {
                EmptyState("No prompts yet. Tap + to save your first prompt.", Icons.Default.Psychology)
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(prompts, key = { it.id }) { prompt ->
                        PromptCard(
                            prompt = prompt,
                            onClick = { onNavigate(Screen.PromptDetail.createRoute(prompt.id)) },
                            onDelete = { showDelete = prompt }
                        )
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }
    }

    showDelete?.let { p ->
        DeleteDialog("Delete \"${p.title}\"?",
            onConfirm = { viewModel.delete(p); showDelete = null },
            onDismiss = { showDelete = null }
        )
    }
}

@Composable
private fun PromptCard(prompt: PromptEntity, onClick: () -> Unit, onDelete: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
            .background(SurfaceVariant).clickable(onClick = onClick).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(prompt.title, color = OnBackground, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            TagChip(prompt.category)
            if (prompt.promptText.isNotBlank()) {
                Text(prompt.promptText.take(100) + "…", color = OnSurfaceVariant, style = MaterialTheme.typography.bodySmall, maxLines = 2)
            }
        }
        IconButton(onClick = onDelete) { Icon(Icons.Default.DeleteOutline, "Delete", tint = OnSurfaceVariant) }
        Icon(Icons.Default.ChevronRight, null, tint = OnSurfaceVariant)
    }
}
