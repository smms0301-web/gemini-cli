package com.mobiapp.ui.process

import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.mobiapp.data.entity.ProcessStepEntity
import com.mobiapp.ui.components.*
import com.mobiapp.ui.theme.*
import com.mobiapp.util.AudioRecorder
import java.io.File

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AddEditStepScreen(
    processId: Long,
    stepId: Long?,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: ProcessViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var voiceNotePath by remember { mutableStateOf("") }
    var imagePaths by remember { mutableStateOf(listOf<String>()) }
    var isEdit by remember { mutableStateOf(false) }
    var isRecording by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(false) }
    val recorder = remember { AudioRecorder(context) }
    var cameraUri by remember { mutableStateOf<Uri?>(null) }

    val audioPermission = rememberPermissionState(Manifest.permission.RECORD_AUDIO)
    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            cameraUri?.path?.let { path -> imagePaths = imagePaths + path }
        }
    }
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        uris.forEach { uri ->
            val destFile = copyUriToFile(context, uri)
            destFile?.let { imagePaths = imagePaths + it.absolutePath }
        }
    }

    LaunchedEffect(stepId) {
        stepId?.let {
            val step = viewModel.getStep(it)
            if (step != null) {
                title = step.title; note = step.note
                voiceNotePath = step.voiceNotePath
                imagePaths = step.imagePaths.split(",").filter { p -> p.isNotBlank() }
                isEdit = true
            }
        }
    }

    DisposableEffect(Unit) { onDispose { recorder.release() } }

    Scaffold(
        containerColor = Background,
        topBar = { MobiTopBar(if (isEdit) "Edit Step" else "Add Step", onBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding)
                .verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MobiTextField(value = title, onValueChange = { title = it }, label = "Step Title")
            MobiTextField(value = note, onValueChange = { note = it }, label = "Notes / Instructions", minLines = 4)

            // Voice note section
            VoiceNoteSection(
                path = voiceNotePath,
                isRecording = isRecording,
                isPlaying = isPlaying,
                onRecord = {
                    if (!audioPermission.status.isGranted) { audioPermission.launchPermissionRequest(); return@VoiceNoteSection }
                    if (isRecording) {
                        val p = recorder.stopRecording(); if (p != null) voiceNotePath = p; isRecording = false
                    } else {
                        val p = AudioRecorder.newRecordingPath(context)
                        if (recorder.startRecording(p)) isRecording = true
                    }
                },
                onPlay = {
                    if (isPlaying) { recorder.stopPlayback(); isPlaying = false }
                    else { recorder.startPlayback(voiceNotePath) { isPlaying = false }; isPlaying = true }
                },
                onDelete = { voiceNotePath = "" }
            )

            // Image attachments
            ImageAttachmentSection(
                paths = imagePaths,
                onCamera = {
                    if (!cameraPermission.status.isGranted) { cameraPermission.launchPermissionRequest(); return@ImageAttachmentSection }
                    val file = File(context.cacheDir, "img_${System.currentTimeMillis()}.jpg")
                    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                    cameraUri = uri
                    cameraLauncher.launch(uri)
                },
                onGallery = { galleryLauncher.launch("image/*") },
                onRemove = { path -> imagePaths = imagePaths - path }
            )

            Button(
                onClick = {
                    if (title.isBlank()) return@Button
                    val step = ProcessStepEntity(
                        id = stepId ?: 0L,
                        processId = processId,
                        stepNumber = 0,
                        title = title.trim(),
                        note = note.trim(),
                        voiceNotePath = voiceNotePath,
                        imagePaths = imagePaths.joinToString(",")
                    )
                    viewModel.saveStep(step) { onSaved() }
                },
                enabled = title.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Amber, contentColor = OnAmber)
            ) { Text(if (isEdit) "Save Changes" else "Add Step") }
        }
    }
}

@Composable
private fun VoiceNoteSection(
    path: String,
    isRecording: Boolean,
    isPlaying: Boolean,
    onRecord: () -> Unit,
    onPlay: () -> Unit,
    onDelete: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Voice Note", color = OnSurfaceVariant, style = MaterialTheme.typography.labelMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = onRecord,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isRecording) ErrorColor else SurfaceVariant,
                    contentColor = if (isRecording) OnBackground else OnSurfaceVariant
                )
            ) {
                Icon(if (isRecording) Icons.Default.Stop else Icons.Default.Mic, null)
                Spacer(Modifier.width(4.dp))
                Text(if (isRecording) "Stop" else "Record")
            }
            if (path.isNotBlank()) {
                Button(
                    onClick = onPlay,
                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceVariant, contentColor = Amber)
                ) {
                    Icon(if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow, null)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.DeleteOutline, "Remove voice note", tint = ErrorColor)
                }
            }
        }
        if (isRecording) Text("Recording…", color = ErrorColor, style = MaterialTheme.typography.labelSmall)
        if (path.isNotBlank() && !isRecording) Text("Voice note saved", color = SuccessColor, style = MaterialTheme.typography.labelSmall)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ImageAttachmentSection(
    paths: List<String>,
    onCamera: () -> Unit,
    onGallery: () -> Unit,
    onRemove: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Images", color = OnSurfaceVariant, style = MaterialTheme.typography.labelMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                onClick = onCamera,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Amber),
                border = androidx.compose.foundation.BorderStroke(1.dp, Outline)
            ) { Icon(Icons.Default.CameraAlt, null); Spacer(Modifier.width(4.dp)); Text("Camera") }
            OutlinedButton(
                onClick = onGallery,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Amber),
                border = androidx.compose.foundation.BorderStroke(1.dp, Outline)
            ) { Icon(Icons.Default.PhotoLibrary, null); Spacer(Modifier.width(4.dp)); Text("Gallery") }
        }
        if (paths.isNotEmpty()) {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                paths.forEach { path ->
                    Box(modifier = Modifier.size(80.dp)) {
                        AsyncImage(
                            model = File(path),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        IconButton(
                            onClick = { onRemove(path) },
                            modifier = Modifier.align(Alignment.TopEnd).size(24.dp)
                        ) {
                            Icon(Icons.Default.Cancel, "Remove", tint = ErrorColor, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }
    }
}

private fun copyUriToFile(context: Context, uri: Uri): File? {
    return try {
        val dir = File(context.filesDir, "images").apply { mkdirs() }
        val dest = File(dir, "img_${System.currentTimeMillis()}.jpg")
        context.contentResolver.openInputStream(uri)?.use { input ->
            dest.outputStream().use { output -> input.copyTo(output) }
        }
        dest
    } catch (e: Exception) { null }
}
