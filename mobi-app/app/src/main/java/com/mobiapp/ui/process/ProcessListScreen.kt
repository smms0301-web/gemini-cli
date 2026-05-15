package com.mobiapp.ui.process

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
import com.mobiapp.data.entity.ProcessEntity
import com.mobiapp.navigation.Screen
import com.mobiapp.ui.components.*
import com.mobiapp.ui.theme.*

@Composable
fun ProcessListScreen(
    onNavigate: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: ProcessViewModel = hiltViewModel()
) {
    val processes by viewModel.filteredProcesses.collectAsStateWithLifecycle()
    val query by viewModel.searchQuery.collectAsStateWithLifecycle()
    var showDelete by remember { mutableStateOf<ProcessEntity?>(null) }

    Scaffold(
        containerColor = Background,
        topBar = { MobiTopBar("Process Tracker", onBack = onBack) },
        floatingActionButton = {
            MobiFab(onClick = { onNavigate(Screen.AddEditProcess.createRoute()) })
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            MobiSearchBar(
                query = query,
                onQueryChange = viewModel::setSearchQuery,
                placeholder = "Search processes…",
                modifier = Modifier.padding(16.dp)
            )
            if (processes.isEmpty()) {
                EmptyState("No processes yet. Tap + to create one.", Icons.Default.AccountTree)
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(processes, key = { it.id }) { process ->
                        ProcessCard(
                            process = process,
                            onClick = { onNavigate(Screen.ProcessDetail.createRoute(process.id)) },
                            onDelete = { showDelete = process }
                        )
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }
    }

    showDelete?.let { process ->
        DeleteDialog(
            title = "Delete \"${process.title}\"?",
            onConfirm = { viewModel.deleteProcess(process); showDelete = null },
            onDismiss = { showDelete = null }
        )
    }
}

@Composable
private fun ProcessCard(
    process: ProcessEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceVariant)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(process.title, color = OnBackground, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                TagChip(process.category)
                TagChip(process.siteTag, color = AmberLight)
            }
        }
        IconButton(onClick = onDelete) {
            Icon(Icons.Default.DeleteOutline, "Delete", tint = OnSurfaceVariant)
        }
        Icon(Icons.Default.ChevronRight, null, tint = OnSurfaceVariant)
    }
}
