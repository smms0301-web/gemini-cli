package com.mobiapp.ui.note

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mobiapp.data.entity.NoteEntity
import com.mobiapp.ui.components.*
import com.mobiapp.ui.theme.*

private val NOTE_COLORS = listOf(
    "none" to NoteAccentNone,
    "amber" to NoteAccentAmber,
    "blue" to NoteAccentBlue,
    "green" to NoteAccentGreen,
    "red" to NoteAccentRed,
    "purple" to NoteAccentPurple
)

@Composable
fun AddEditNoteScreen(
    noteId: Long?,
    onBack: () -> Unit,
    onSaved: (Long) -> Unit,
    viewModel: NoteViewModel = hiltViewModel()
) {
    var title by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }
    var colorAccent by remember { mutableStateOf("none") }
    var isPinned by remember { mutableStateOf(false) }
    var isEdit by remember { mutableStateOf(false) }

    LaunchedEffect(noteId) {
        noteId?.let { id ->
            val n = viewModel.getById(id) ?: return@let
            title = n.title; body = n.body; colorAccent = n.colorAccent; isPinned = n.isPinned; isEdit = true
        }
    }

    Scaffold(
        containerColor = Background,
        topBar = {
            MobiTopBar(if (isEdit) "Edit Note" else "New Note", onBack = onBack, actions = {
                IconButton(onClick = { isPinned = !isPinned }) {
                    Icon(Icons.Default.PushPin, null, tint = if (isPinned) Amber else OnSurfaceVariant)
                }
            })
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding)
                .verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MobiTextField(value = title, onValueChange = { title = it }, label = "Title")
            MobiTextField(value = body, onValueChange = { body = it }, label = "Note", minLines = 8)

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Color:", color = OnSurfaceVariant, style = MaterialTheme.typography.labelMedium)
                NOTE_COLORS.forEach { (key, color) ->
                    Box(
                        modifier = Modifier
                            .size(28.dp).clip(CircleShape)
                            .background(if (color == NoteAccentNone) SurfaceContainer else color)
                            .border(if (colorAccent == key) 2.dp else 0.5.dp,
                                if (colorAccent == key) Amber else Outline, CircleShape)
                            .clickable { colorAccent = key }
                    )
                }
            }

            Button(
                onClick = {
                    if (title.isBlank()) return@Button
                    viewModel.save(
                        NoteEntity(id = noteId ?: 0L, title = title.trim(), body = body.trim(), colorAccent = colorAccent, isPinned = isPinned),
                        onSaved
                    )
                },
                enabled = title.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Amber, contentColor = OnAmber)
            ) { Text(if (isEdit) "Save Changes" else "Save Note") }
        }
    }
}
