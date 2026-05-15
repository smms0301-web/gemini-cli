package com.mobiapp.ui.note

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mobiapp.data.entity.NoteEntity
import com.mobiapp.navigation.Screen
import com.mobiapp.ui.components.*
import com.mobiapp.ui.theme.*

@Composable
fun NoteDetailScreen(
    noteId: Long,
    onNavigate: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: NoteViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var note by remember { mutableStateOf<NoteEntity?>(null) }
    LaunchedEffect(noteId) { note = viewModel.getById(noteId) }

    Scaffold(
        containerColor = Background,
        topBar = {
            MobiTopBar(note?.title ?: "Note", onBack = onBack, actions = {
                note?.let { n ->
                    IconButton(onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, "${n.title}\n\n${n.body}")
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Note"))
                    }) { Icon(Icons.Default.Share, "Share", tint = OnSurfaceVariant) }
                    IconButton(onClick = { viewModel.togglePin(n) }) {
                        Icon(Icons.Default.PushPin, null, tint = if (n.isPinned) Amber else OnSurfaceVariant)
                    }
                    IconButton(onClick = { onNavigate(Screen.AddEditNote.createRoute(noteId)) }) {
                        Icon(Icons.Default.Edit, "Edit", tint = Amber)
                    }
                }
            })
        }
    ) { padding ->
        note?.let { n ->
            Column(
                modifier = Modifier.fillMaxSize().padding(padding)
                    .verticalScroll(rememberScrollState()).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(n.title, color = OnBackground, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                HorizontalDivider(color = Outline)
                Text(n.body, color = OnBackground, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}
