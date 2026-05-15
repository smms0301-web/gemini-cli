package com.mobiapp.ui.tool

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
import com.mobiapp.data.entity.ToolEntity
import com.mobiapp.navigation.Screen
import com.mobiapp.ui.components.*
import com.mobiapp.ui.theme.*

@Composable
fun ToolListScreen(
    onNavigate: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: ToolViewModel = hiltViewModel()
) {
    val tools by viewModel.filtered.collectAsStateWithLifecycle()
    val query by viewModel.query.collectAsStateWithLifecycle()
    var showDelete by remember { mutableStateOf<ToolEntity?>(null) }

    Scaffold(
        containerColor = Background,
        topBar = { MobiTopBar("AI Tools Directory", onBack = onBack) },
        floatingActionButton = { MobiFab(onClick = { onNavigate(Screen.AddEditTool.createRoute()) }) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                MobiSearchBar(query, viewModel::setQuery, "Search tags (e.g. \"video audio\")…")
                Text("Type multiple words to filter by multiple tags (AND filter)", color = OnSurfaceVariant, style = MaterialTheme.typography.labelSmall)
            }
            if (tools.isEmpty()) {
                EmptyState("No tools match. Try different tags or add a tool.", Icons.Default.Build)
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(tools, key = { it.id }) { tool ->
                        ToolCard(
                            tool = tool,
                            onClick = { onNavigate(Screen.ToolDetail.createRoute(tool.id)) },
                            onDelete = { showDelete = tool }
                        )
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }
    }

    showDelete?.let { t ->
        DeleteDialog("Delete \"${t.name}\"?",
            onConfirm = { viewModel.delete(t); showDelete = null },
            onDismiss = { showDelete = null }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ToolCard(tool: ToolEntity, onClick: () -> Unit, onDelete: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
            .background(SurfaceVariant).clickable(onClick = onClick).padding(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(tool.name, color = OnBackground, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            if (tool.description.isNotBlank()) {
                Text(tool.description.take(120) + if (tool.description.length > 120) "…" else "",
                    color = OnSurfaceVariant, style = MaterialTheme.typography.bodySmall, maxLines = 2)
            }
            FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                tool.tags.split(",").filter { it.isNotBlank() }.forEach { tag ->
                    TagChip(tag.trim())
                }
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            IconButton(onClick = onDelete) { Icon(Icons.Default.DeleteOutline, "Delete", tint = OnSurfaceVariant) }
            Icon(Icons.Default.ChevronRight, null, tint = OnSurfaceVariant)
        }
    }
}
