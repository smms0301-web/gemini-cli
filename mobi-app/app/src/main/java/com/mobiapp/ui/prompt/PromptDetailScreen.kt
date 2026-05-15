package com.mobiapp.ui.prompt

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
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
import com.mobiapp.data.entity.PromptEntity
import com.mobiapp.navigation.Screen
import com.mobiapp.ui.components.*
import com.mobiapp.ui.theme.*

@Composable
fun PromptDetailScreen(
    promptId: Long,
    onNavigate: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: PromptViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var prompt by remember { mutableStateOf<PromptEntity?>(null) }

    LaunchedEffect(promptId) { prompt = viewModel.getById(promptId) }

    Scaffold(
        containerColor = Background,
        topBar = {
            MobiTopBar(
                title = prompt?.title ?: "Prompt",
                onBack = onBack,
                actions = {
                    prompt?.let { p ->
                        IconButton(onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("prompt", p.promptText))
                            Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(Icons.Default.ContentCopy, "Copy", tint = Amber)
                        }
                        IconButton(onClick = { onNavigate(Screen.AddEditPrompt.createRoute(promptId)) }) {
                            Icon(Icons.Default.Edit, "Edit", tint = OnSurfaceVariant)
                        }
                    }
                }
            )
        }
    ) { padding ->
        prompt?.let { p ->
            Column(
                modifier = Modifier.fillMaxSize().padding(padding)
                    .verticalScroll(rememberScrollState()).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TagChip(p.category)
                }
                MobiCard {
                    Text("Prompt Text", color = Amber, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text(p.promptText, color = OnBackground, style = MaterialTheme.typography.bodyMedium)
                }
                if (p.personalNote.isNotBlank()) {
                    MobiCard {
                        Text("Personal Note", color = Amber, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        Text(p.personalNote, color = OnSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
                    }
                }
                Button(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("prompt", p.promptText))
                        Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Amber, contentColor = OnAmber)
                ) {
                    Icon(Icons.Default.ContentCopy, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Copy Prompt to Clipboard", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
