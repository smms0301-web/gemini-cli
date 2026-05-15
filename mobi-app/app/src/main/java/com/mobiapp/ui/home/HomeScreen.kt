package com.mobiapp.ui.home

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mobiapp.navigation.Screen
import com.mobiapp.ui.components.*
import com.mobiapp.ui.theme.*

@Composable
fun HomeScreen(
    onNavigate: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .systemBarsPadding()
    ) {
        // App header
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("MOBI", color = Amber, fontSize = 28.sp, fontWeight = FontWeight.Black, letterSpacing = 4.sp)
                Text("Personal Command Center", color = OnSurfaceVariant, style = MaterialTheme.typography.labelSmall)
            }
            IconButton(onClick = { onNavigate(Screen.Settings.route) }) {
                Icon(Icons.Default.Settings, "Settings", tint = OnSurfaceVariant)
            }
        }

        // Master search bar
        MobiSearchBar(
            query = state.searchQuery,
            onQueryChange = viewModel::onSearchQueryChange,
            placeholder = "Search everything…",
            modifier = Modifier.padding(horizontal = 20.dp).padding(bottom = 16.dp)
        )

        if (state.isSearching) {
            SearchResultsView(results = state.searchResults, onResultClick = { result ->
                when (result.module) {
                    "Process" -> onNavigate(Screen.ProcessDetail.createRoute(result.id))
                    "Reminder" -> onNavigate(Screen.ReminderList.route)
                    "Prompt" -> onNavigate(Screen.PromptDetail.createRoute(result.id))
                    "Tool" -> onNavigate(Screen.ToolDetail.createRoute(result.id))
                    "Note" -> onNavigate(Screen.NoteDetail.createRoute(result.id))
                }
            })
        } else {
            ModuleGrid(state = state, onNavigate = onNavigate)
        }
    }
}

@Composable
private fun SearchResultsView(
    results: Map<String, List<SearchResult>>,
    onResultClick: (SearchResult) -> Unit
) {
    if (results.isEmpty()) {
        EmptyState("No results found", Icons.Default.SearchOff)
        return
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        results.forEach { (module, items) ->
            item {
                Text(
                    module.uppercase(),
                    color = Amber,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                )
            }
            items(items) { result ->
                SearchResultCard(result = result, onClick = { onResultClick(result) })
            }
            item { HorizontalDivider(color = Outline, modifier = Modifier.padding(vertical = 4.dp)) }
        }
    }
}

@Composable
private fun SearchResultCard(result: SearchResult, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(SurfaceVariant)
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(result.title, color = OnBackground, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            if (result.snippet.isNotBlank()) {
                Text(result.snippet, color = OnSurfaceVariant, style = MaterialTheme.typography.bodySmall, maxLines = 2)
            }
            if (result.tags.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    result.tags.split(",").take(3).forEach { tag ->
                        TagChip(tag.trim(), modifier = Modifier)
                    }
                }
            }
        }
        Icon(Icons.Default.ChevronRight, null, tint = OnSurfaceVariant, modifier = Modifier.size(20.dp))
    }
}

@Composable
private fun ModuleGrid(state: HomeUiState, onNavigate: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            ModuleCard(
                title = "Process Tracker",
                subtitle = "Step-by-step guides & documentation",
                stat = "${state.processCount} processes",
                icon = Icons.Default.AccountTree,
                onClick = { onNavigate(Screen.ProcessList.route) }
            )
        }
        item {
            ModuleCard(
                title = "Smart Reminders",
                subtitle = "Scheduled notifications & alerts",
                stat = "${state.reminderCount} active",
                icon = Icons.Default.Notifications,
                onClick = { onNavigate(Screen.ReminderList.route) }
            )
        }
        item {
            ModuleCard(
                title = "AI Prompts Library",
                subtitle = "Personal collection of AI prompts",
                stat = "${state.promptCount} prompts saved",
                icon = Icons.Default.Psychology,
                onClick = { onNavigate(Screen.PromptList.route) }
            )
        }
        item {
            ModuleCard(
                title = "AI Tools Directory",
                subtitle = "Curated reference of useful AI tools",
                stat = "${state.toolCount} tools catalogued",
                icon = Icons.Default.Build,
                onClick = { onNavigate(Screen.ToolList.route) }
            )
        }
        item {
            ModuleCard(
                title = "Quick Notes",
                subtitle = "Instant capture & scratchpad",
                stat = "${state.noteCount} notes",
                icon = Icons.Default.StickyNote2,
                onClick = { onNavigate(Screen.NoteList.route) }
            )
        }
        item { Spacer(Modifier.height(80.dp)) }
    }
}

@Composable
private fun ModuleCard(
    title: String,
    subtitle: String,
    stat: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceVariant)
            .clickable(onClick = onClick)
            .padding(18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Amber.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = Amber, modifier = Modifier.size(26.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = OnBackground, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(subtitle, color = OnSurfaceVariant, style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(4.dp))
            Text(stat, color = Amber, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Medium)
        }
        Icon(Icons.Default.ChevronRight, null, tint = OnSurfaceVariant)
    }
}
