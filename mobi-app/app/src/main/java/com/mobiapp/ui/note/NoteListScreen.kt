package com.mobiapp.ui.note

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mobiapp.data.entity.NoteEntity
import com.mobiapp.ui.components.*
import com.mobiapp.ui.theme.Amber

@Composable
fun NoteListScreen(
    onBack: () -> Unit,
    onAddNote: () -> Unit,
    onNoteClick: (Long) -> Unit,
    viewModel: NoteViewModel = hiltViewModel()
) {
    val query by viewModel.query.collectAsState()
    val notes by viewModel.notes.collectAsState()
    var deleteTarget by remember { mutableStateOf<NoteEntity?>(null) }

    Scaffold(
        topBar = { MobiTopBar("Quick Notes", onBack = onBack) },
        floatingActionButton = { MobiFab(onAddNote) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            MobiSearchBar(query = query, onQueryChange = { viewModel.query.value = it })
            Spacer(Modifier.height(12.dp))
            if (notes.isEmpty()) {
                EmptyState("No notes yet. Tap + to create one.")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(notes) { note ->
                        Card(
                            onClick = { onNoteClick(note.id) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = try { Color(android.graphics.Color.parseColor(note.colorHex)) }
                                catch (e: Exception) { MaterialTheme.colorScheme.surfaceVariant }
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(Modifier.weight(1f)) {
                                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                                        if (note.isPinned) {
                                            Icon(Icons.Default.PushPin, contentDescription = null, tint = Amber, modifier = Modifier.size(14.dp))
                                            Spacer(Modifier.width(4.dp))
                                        }
                                        Text(note.title, style = MaterialTheme.typography.titleMedium)
                                    }
                                    if (note.content.isNotBlank()) {
                                        Text(note.content, style = MaterialTheme.typography.bodySmall, maxLines = 3)
                                    }
                                }
                                Column {
                                    IconButton(onClick = { viewModel.togglePin(note) }) {
                                        Icon(Icons.Default.PushPin, contentDescription = "Pin", tint = if (note.isPinned) Amber else MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    IconButton(onClick = { deleteTarget = note }) {
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

    deleteTarget?.let { note ->
        DeleteDialog(
            message = "Delete \"${note.title}\"?",
            onConfirm = { viewModel.deleteNote(note); deleteTarget = null },
            onDismiss = { deleteTarget = null }
        )
    }
}
