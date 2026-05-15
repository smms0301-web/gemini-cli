package com.mobiapp.ui.process

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.mobiapp.data.entity.ProcessStepEntity
import com.mobiapp.navigation.Screen
import com.mobiapp.ui.components.*
import com.mobiapp.ui.theme.*
import com.mobiapp.util.AudioRecorder
import java.io.File

@Composable
fun StepDetailScreen(
    stepId: Long,
    processId: Long,
    onNavigate: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: ProcessViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var step by remember { mutableStateOf<ProcessStepEntity?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    val recorder = remember { AudioRecorder(context) }

    LaunchedEffect(stepId) { step = viewModel.getStep(stepId) }
    DisposableEffect(Unit) { onDispose { recorder.release() } }

    val imagePaths = step?.imagePaths?.split(",")?.filter { it.isNotBlank() } ?: emptyList()

    Scaffold(
        containerColor = Background,
        topBar = {
            MobiTopBar(
                title = "Step ${step?.stepNumber ?: ""}",
                onBack = onBack,
                actions = {
                    step?.let { s ->
                        IconButton(onClick = { viewModel.toggleStepDone(s.id, !s.isDone) }) {
                            Icon(
                                if (s.isDone) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                null, tint = if (s.isDone) Amber else OnSurfaceVariant
                            )
                        }
                        IconButton(onClick = { onNavigate(Screen.AddEditStep.createRoute(processId, stepId)) }) {
                            Icon(Icons.Default.Edit, "Edit", tint = OnSurfaceVariant)
                        }
                    }
                }
            )
        }
    ) { padding ->
        step?.let { s ->
            Column(
                modifier = Modifier.fillMaxSize().padding(padding)
                    .verticalScroll(rememberScrollState()).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MobiCard {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TagChip("Step ${s.stepNumber}")
                        if (s.isDone) TagChip("Done", color = SuccessColor)
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(s.title, color = OnBackground, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                }

                if (s.note.isNotBlank()) {
                    MobiCard {
                        Text("Notes", color = Amber, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        Text(s.note, color = OnBackground, style = MaterialTheme.typography.bodyMedium, lineHeight = androidx.compose.ui.unit.TextUnit.Unspecified)
                    }
                }

                if (s.voiceNotePath.isNotBlank()) {
                    MobiCard {
                        Text("Voice Note", color = Amber, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        Button(
                            onClick = {
                                if (isPlaying) { recorder.stopPlayback(); isPlaying = false }
                                else { recorder.startPlayback(s.voiceNotePath) { isPlaying = false }; isPlaying = true }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SurfaceContainer, contentColor = Amber)
                        ) {
                            Icon(if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow, null)
                            Spacer(Modifier.width(8.dp))
                            Text(if (isPlaying) "Stop" else "Play Voice Note")
                        }
                    }
                }

                if (imagePaths.isNotEmpty()) {
                    MobiCard {
                        Text("Images (${imagePaths.size})", color = Amber, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        imagePaths.forEach { path ->
                            AsyncImage(
                                model = File(path),
                                contentDescription = null,
                                modifier = Modifier.fillMaxWidth().height(220.dp).clip(RoundedCornerShape(10.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                }
            }
        } ?: Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Amber)
        }
    }
}
