package com.mobiapp.ui.note

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mobiapp.ui.components.MobiTopBar
import com.mobiapp.ui.theme.Amber

@Composable
fun NoteDetailScreen(
    noteId: Long,
    onBack: () -> Unit,
    onEdit: (Long) -> Unit,
    viewModel: NoteViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val note by viewModel.getNote(noteId).collectAsState(initial = null)

    Scaffold(
        topBar = {
            MobiTopBar(
                title = note?.title ?: "Note",
                onBack = onBack,
                actions = {
                    IconButton(onClick = {
                        note?.let {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, it.title)
                                putExtra(Intent.EXTRA_TEXT, "${it.title}\n\n${it.content}")
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share Note"))
                        }
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = Amber)
                    }
                    IconButton(onClick = { onEdit(noteId) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Amber)
                    }
                }
            )
        }
    ) { padding ->
        note?.let { n ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(n.content, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}
