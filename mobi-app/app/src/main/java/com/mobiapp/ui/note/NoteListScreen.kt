package com.mobiapp.ui.note

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mobiapp.data.entity.NoteEntity
import com.mobiapp.navigation.Screen
import com.mobiapp.ui.components.*
import com.mobiapp.ui.theme.*

@Composable
fun NoteListScreen(
    onNavigate: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: NoteViewModel = hiltViewModel()
) {
    val notes by viewModel.filtered.collectAsStateWithLifecycle()
    val query by viewModel.query.collectAsStateWithLifecycle()
    var showDelete by remember { mutableStateOf<NoteEntity?>(null) }

    Scaffold(
        containerColor = Background,
        topBar = { MobiTopBar("Quick Notes", onBack = onBack) },
        floatingActionButton = { MobiFab(onClick = { onNavigate(Screen.AddEditNote.createRoute()) }) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            MobiSearchBar(query, viewModel::setQuery, "Search notes…", Modifier.padding(16.dp))
            if (notes.isEmpty()) {
                EmptyState("No notes yet. Tap + to jot something down.", Icons.Default.StickyNote2)
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(notes, key = { it.id }) { note ->
                        NoteCard(
                            note = note,
                            onClick = { onNavigate(Screen.NoteDetail.createRoute(note.id)) },
                            onPin = { viewModel.togglePin(note) },
                            onDelete = { showDelete = note }
                        )
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }
    }

    showDelete?.let { n ->
        DeleteDialog("Delete \"${n.title}\"?",
            onConfirm = { viewModel.delete(n); showDelete = null },
            onDismiss = { showDelete = null }
        )
    }
}

@Composable
private fun NoteCard(note: NoteEntity, onClick: () -> Unit, onPin: () -> Unit, onDelete: () -> Unit) {
    val accentColor = noteAccentColor(note.colorAccent)

    Row(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(accentColor)
            .border(0.5.dp, if (note.isPinned) Amber else Outline, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                if (note.isPinned) Icon(Icons.Default.PushPin, null, tint = Amber, modifier = Modifier.size(14.dp))
                Text(note.title, color = OnBackground, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            }
            if (note.body.isNotBlank()) {
                Text(note.body.take(120) + if (note.body.length > 120) "…" else "",
                    color = OnSurfaceVariant, style = MaterialTheme.typography.bodySmall, maxLines = 3)
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            IconButton(onClick = onPin, modifier = Modifier.size(36.dp)) {
                Icon(if (note.isPinned) Icons.Default.PushPin else Icons.Default.PushPin,
                    null, tint = if (note.isPinned) Amber else OnSurfaceVariant, modifier = Modifier.size(18.dp))
            }
            IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Default.DeleteOutline, "Delete", tint = OnSurfaceVariant, modifier = Modifier.size(18.dp))
            }
        }
    }
}

fun noteAccentColor(accent: String): Color = when (accent) {
    "amber" -> NoteAccentAmber
    "blue" -> NoteAccentBlue
    "green" -> NoteAccentGreen
    "red" -> NoteAccentRed
    "purple" -> NoteAccentPurple
    else -> SurfaceVariant
}
