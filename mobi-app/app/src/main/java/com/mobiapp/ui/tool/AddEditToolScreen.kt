package com.mobiapp.ui.tool

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mobiapp.data.entity.ToolEntity
import com.mobiapp.ui.components.*
import com.mobiapp.ui.process.*
import com.mobiapp.ui.theme.*

private val ALL_TAGS = listOf("Video", "Audio", "Image", "Writing", "Code", "Translation", "Presentation", "Research", "Data", "Voice", "Design", "Security", "AI", "Automation", "Productivity")

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddEditToolScreen(
    toolId: Long?,
    onBack: () -> Unit,
    viewModel: ToolViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedTags by remember { mutableStateOf(setOf<String>()) }
    var customTagInput by remember { mutableStateOf("") }
    var isEdit by remember { mutableStateOf(false) }

    LaunchedEffect(toolId) {
        toolId?.let { id ->
            val t = viewModel.getById(id) ?: return@let
            name = t.name; description = t.description
            selectedTags = t.tags.split(",").map { it.trim() }.filter { it.isNotBlank() }.toSet()
            isEdit = true
        }
    }

    Scaffold(
        containerColor = Background,
        topBar = { MobiTopBar(if (isEdit) "Edit Tool" else "Add Tool", onBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding)
                .verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MobiTextField(value = name, onValueChange = { name = it }, label = "Tool Name")
            MobiTextField(value = description, onValueChange = { description = it }, label = "Description & When to Use", minLines = 3)

            Text("Tags", color = OnSurfaceVariant, style = MaterialTheme.typography.labelMedium)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ALL_TAGS.forEach { tag ->
                    FilterChip(
                        selected = tag in selectedTags,
                        onClick = { selectedTags = if (tag in selectedTags) selectedTags - tag else selectedTags + tag },
                        label = { Text(tag) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Amber.copy(alpha = 0.2f),
                            selectedLabelColor = Amber,
                            containerColor = SurfaceVariant,
                            labelColor = OnSurfaceVariant
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = Outline, selectedBorderColor = Amber, enabled = true, selected = tag in selectedTags
                        )
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MobiTextField(
                    value = customTagInput,
                    onValueChange = { customTagInput = it },
                    label = "Custom tag",
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = {
                        val t = customTagInput.trim()
                        if (t.isNotBlank()) { selectedTags = selectedTags + t; customTagInput = "" }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceVariant, contentColor = Amber)
                ) { Text("Add") }
            }

            if (selectedTags.isNotEmpty()) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    selectedTags.filter { it !in ALL_TAGS }.forEach { tag ->
                        TagChip(tag, onClick = { selectedTags = selectedTags - tag })
                    }
                }
            }

            Button(
                onClick = {
                    if (name.isBlank()) return@Button
                    viewModel.save(
                        ToolEntity(id = toolId ?: 0L, name = name.trim(), description = description.trim(), tags = selectedTags.joinToString(",")),
                        onBack
                    )
                },
                enabled = name.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Amber, contentColor = OnAmber)
            ) { Text(if (isEdit) "Save Changes" else "Add Tool") }
        }
    }
}
