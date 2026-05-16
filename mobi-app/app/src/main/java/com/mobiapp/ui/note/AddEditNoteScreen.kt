package com.mobiapp.ui.note

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mobiapp.data.entity.NoteEntity
import com.mobiapp.ui.components.MobiTopBar
import com.mobiapp.ui.theme.Amber
import com.mobiapp.ui.theme.NoteColors

@Composable
fun AddEditNoteScreen(
    noteId: Long?,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: NoteViewModel = hiltViewModel()
) {
    val existing by viewModel.getNote(noteId ?: -1L).collectAsState(initial = null)
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(NoteColors[0]) }
    var initialized by remember { mutableStateOf(false) }

    LaunchedEffect(existing) {
        if (!initialized && noteId != null && existing != null) {
            title = existing!!.title
            content = existing!!.content
            try { selectedColor = Color(android.graphics.Color.parseColor(existing!!.colorHex)) } catch (_: Exception) {}
            initialized = true
        }
    }

    Scaffold(
        topBar = { MobiTopBar(if (noteId == null) "New Note" else "Edit Note", onBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = title, onValueChange = { title = it },
                label = { Text("Title") }, modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Amber)
            )
            OutlinedTextField(
                value = content, onValueChange = { content = it },
                label = { Text("Content") },
                modifier = Modifier.fillMaxWidth().height(200.dp),
                maxLines = 12,
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Amber)
            )

            Text("Note Color", style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                NoteColors.forEach { color ->
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(color)
                            .then(
                                if (color == selectedColor)
                                    Modifier.border(2.dp, Amber, CircleShape)
                                else Modifier
                            )
                            .clickable { selectedColor = color }
                    )
                }
            }

            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val colorHex = "#%06X".format(0xFFFFFF and selectedColor.value.toInt())
                        val entity = NoteEntity(
                            id = noteId ?: 0L,
                            title = title.trim(),
                            content = content.trim(),
                            colorHex = colorHex,
                            isPinned = existing?.isPinned ?: false
                        )
                        viewModel.saveNote(entity) { onSaved() }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Amber)
            ) {
                Text("Save", color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}
