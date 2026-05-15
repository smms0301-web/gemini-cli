package com.mobiapp.ui.tool

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mobiapp.data.entity.ToolEntity
import com.mobiapp.navigation.Screen
import com.mobiapp.ui.components.*
import com.mobiapp.ui.theme.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ToolDetailScreen(
    toolId: Long,
    onNavigate: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: ToolViewModel = hiltViewModel()
) {
    var tool by remember { mutableStateOf<ToolEntity?>(null) }
    LaunchedEffect(toolId) { tool = viewModel.getById(toolId) }

    Scaffold(
        containerColor = Background,
        topBar = {
            MobiTopBar(tool?.name ?: "Tool", onBack = onBack, actions = {
                tool?.let {
                    IconButton(onClick = { onNavigate(Screen.AddEditTool.createRoute(toolId)) }) {
                        Icon(Icons.Default.Edit, "Edit", tint = Amber)
                    }
                }
            })
        }
    ) { padding ->
        tool?.let { t ->
            Column(
                modifier = Modifier.fillMaxSize().padding(padding)
                    .verticalScroll(rememberScrollState()).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MobiCard {
                    Text("Description", color = Amber, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text(t.description, color = OnBackground, style = MaterialTheme.typography.bodyMedium)
                }
                MobiCard {
                    Text("Tags", color = Amber, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        t.tags.split(",").filter { it.isNotBlank() }.forEach { tag ->
                            TagChip(tag.trim())
                        }
                    }
                }
            }
        }
    }
}
