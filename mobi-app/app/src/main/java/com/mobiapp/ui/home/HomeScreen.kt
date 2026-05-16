package com.mobiapp.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mobiapp.ui.components.MobiCard
import com.mobiapp.ui.components.MobiSearchBar
import com.mobiapp.ui.components.MobiTopBar
import com.mobiapp.ui.theme.Amber

@Composable
fun HomeScreen(
    onNavigateToProcess: () -> Unit,
    onNavigateToReminder: () -> Unit,
    onNavigateToPrompt: () -> Unit,
    onNavigateToTool: () -> Unit,
    onNavigateToNote: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val query by viewModel.query.collectAsState()
    val results by viewModel.results.collectAsState()

    Scaffold(
        topBar = {
            MobiTopBar(
                title = "Mobi App",
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Amber)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                MobiSearchBar(
                    query = query,
                    onQueryChange = viewModel::onQueryChange,
                    placeholder = "Search everything..."
                )
            }

            if (query.isNotBlank() && results.isNotEmpty()) {
                results.forEach { (module, items) ->
                    item {
                        Text(module, style = MaterialTheme.typography.labelLarge, color = Amber)
                        Spacer(Modifier.height(4.dp))
                    }
                    items(items) { result ->
                        MobiCard(onClick = {}) {
                            Text(result.title, style = MaterialTheme.typography.titleMedium)
                            if (result.subtitle.isNotBlank()) {
                                Text(result.subtitle, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            } else if (query.isBlank()) {
                item {
                    Text(
                        "Quick Access",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Amber
                    )
                }
                item {
                    ModuleGrid(
                        onNavigateToProcess = onNavigateToProcess,
                        onNavigateToReminder = onNavigateToReminder,
                        onNavigateToPrompt = onNavigateToPrompt,
                        onNavigateToTool = onNavigateToTool,
                        onNavigateToNote = onNavigateToNote
                    )
                }
            }
        }
    }
}

@Composable
private fun ModuleGrid(
    onNavigateToProcess: () -> Unit,
    onNavigateToReminder: () -> Unit,
    onNavigateToPrompt: () -> Unit,
    onNavigateToTool: () -> Unit,
    onNavigateToNote: () -> Unit
) {
    val modules = listOf(
        Triple("Process Tracker", Icons.Default.AccountTree, onNavigateToProcess),
        Triple("Reminders", Icons.Default.Alarm, onNavigateToReminder),
        Triple("AI Prompts", Icons.Default.AutoAwesome, onNavigateToPrompt),
        Triple("AI Tools", Icons.Default.Build, onNavigateToTool),
        Triple("Quick Notes", Icons.Default.Note, onNavigateToNote)
    )
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        modules.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                row.forEach { (label, icon, action) ->
                    ModuleCard(
                        label = label,
                        icon = icon,
                        onClick = action,
                        modifier = Modifier.weight(1f)
                    )
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun ModuleCard(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(icon, contentDescription = null, tint = Amber, modifier = Modifier.size(28.dp))
            Text(label, style = MaterialTheme.typography.titleMedium)
        }
    }
}
