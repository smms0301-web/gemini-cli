package com.mobiapp.ui.process

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.mobiapp.ui.components.*
import com.mobiapp.ui.theme.*
import com.mobiapp.util.PdfExportOptions

@Composable
fun PdfExportScreen(
    processId: Long,
    onBack: () -> Unit,
    viewModel: ProcessViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var includeNotes by remember { mutableStateOf(true) }
    var includeStatus by remember { mutableStateOf(true) }
    var includeTimestamps by remember { mutableStateOf(false) }
    var includePhotos by remember { mutableStateOf(true) }
    var isExporting by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Background,
        topBar = { MobiTopBar("Export PDF", onBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Choose what to include in the PDF:", color = OnBackground, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            PdfOptionToggle("Step Notes & Instructions", includeNotes) { includeNotes = it }
            PdfOptionToggle("Step Completion Status", includeStatus) { includeStatus = it }
            PdfOptionToggle("Timestamps", includeTimestamps) { includeTimestamps = it }
            PdfOptionToggle("Attached Photos", includePhotos) { includePhotos = it }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = {
                    isExporting = true
                    viewModel.exportPdf(
                        context, processId,
                        PdfExportOptions(includeNotes, includeStatus, includeTimestamps, includePhotos)
                    ) { file ->
                        isExporting = false
                        if (file != null) {
                            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                setDataAndType(uri, "application/pdf")
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            val shareIntent = Intent.createChooser(
                                Intent(Intent.ACTION_SEND).apply {
                                    type = "application/pdf"
                                    putExtra(Intent.EXTRA_STREAM, uri)
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }, "Share or Save PDF"
                            )
                            try { context.startActivity(shareIntent) }
                            catch (e: Exception) { Toast.makeText(context, "PDF saved to: ${file.absolutePath}", Toast.LENGTH_LONG).show() }
                        } else {
                            Toast.makeText(context, "Failed to generate PDF", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                enabled = !isExporting,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Amber, contentColor = OnAmber)
            ) {
                if (isExporting) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = OnAmber, strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Default.PictureAsPdf, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Generate & Share PDF", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun PdfOptionToggle(label: String, checked: Boolean, onChecked: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().background(SurfaceVariant, androidx.compose.foundation.shape.RoundedCornerShape(10.dp)).padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Text(label, color = OnBackground, style = MaterialTheme.typography.bodyMedium)
        Switch(
            checked = checked,
            onCheckedChange = onChecked,
            colors = SwitchDefaults.colors(checkedThumbColor = OnAmber, checkedTrackColor = Amber, uncheckedTrackColor = SurfaceContainer)
        )
    }
}
