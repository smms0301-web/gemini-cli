package com.mobiapp.ui.process

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.mobiapp.ui.components.MobiTopBar
import com.mobiapp.ui.theme.Amber

@Composable
fun PdfExportScreen(
    processId: Long,
    onBack: () -> Unit,
    viewModel: ProcessViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var exporting by remember { mutableStateOf(false) }
    var exported by remember { mutableStateOf(false) }
    var filePath by remember { mutableStateOf("") }

    Scaffold(
        topBar = { MobiTopBar("Export PDF", onBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (exported) {
                Text("PDF exported!", style = MaterialTheme.typography.titleMedium, color = Amber)
                Spacer(Modifier.height(8.dp))
                Text(filePath, style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = {
                        val file = java.io.File(filePath)
                        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "application/pdf"
                            putExtra(Intent.EXTRA_STREAM, uri)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share PDF"))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Amber)
                ) {
                    Text("Share PDF", color = MaterialTheme.colorScheme.onPrimary)
                }
            } else {
                Text("Export this process as a PDF document.", style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = {
                        exporting = true
                        viewModel.exportPdf(context, processId) { file ->
                            exporting = false
                            if (file != null) {
                                filePath = file.absolutePath
                                exported = true
                            }
                        }
                    },
                    enabled = !exporting,
                    colors = ButtonDefaults.buttonColors(containerColor = Amber)
                ) {
                    if (exporting) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Text("Generate PDF", color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            }
        }
    }
}
