package com.mobiapp.ui.process

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mobiapp.data.entity.ProcessEntity
import com.mobiapp.ui.components.*
import com.mobiapp.ui.theme.*

private val CATEGORIES = listOf("CCTV", "Access Control", "Network", "General", "IT", "Security", "Other")
private val SITES = listOf("HQ", "Site-01", "Site-02", "Site-03", "Remote", "All Sites")

@Composable
fun AddEditProcessScreen(
    processId: Long?,
    onBack: () -> Unit,
    onSaved: (Long) -> Unit,
    viewModel: ProcessViewModel = hiltViewModel()
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(CATEGORIES.first()) }
    var site by remember { mutableStateOf(SITES.first()) }
    var isEdit by remember { mutableStateOf(false) }

    LaunchedEffect(processId) {
        processId?.let {
            val p = viewModel.getProcess(it)
            if (p != null) {
                title = p.title; description = p.description
                category = p.category; site = p.siteTag; isEdit = true
            }
        }
    }

    Scaffold(
        containerColor = Background,
        topBar = {
            MobiTopBar(
                title = if (isEdit) "Edit Process" else "New Process",
                onBack = onBack
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding)
                .verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MobiTextField(value = title, onValueChange = { title = it }, label = "Process Title")
            MobiTextField(value = description, onValueChange = { description = it }, label = "Description (optional)", minLines = 3)

            Text("Category", color = OnSurfaceVariant, style = MaterialTheme.typography.labelMedium)
            ChipSelector(options = CATEGORIES, selected = category, onSelected = { category = it })

            Text("Site", color = OnSurfaceVariant, style = MaterialTheme.typography.labelMedium)
            ChipSelector(options = SITES, selected = site, onSelected = { site = it })

            Button(
                onClick = {
                    if (title.isBlank()) return@Button
                    val entity = ProcessEntity(
                        id = processId ?: 0L,
                        title = title.trim(),
                        description = description.trim(),
                        category = category,
                        siteTag = site
                    )
                    viewModel.saveProcess(entity, onSaved)
                },
                enabled = title.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Amber, contentColor = OnAmber)
            ) { Text(if (isEdit) "Save Changes" else "Create Process") }
        }
    }
}

@Composable
fun MobiTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    minLines: Int = 1,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        minLines = minLines,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Amber,
            unfocusedBorderColor = Outline,
            focusedLabelColor = Amber,
            unfocusedLabelColor = OnSurfaceVariant,
            cursorColor = Amber,
            focusedTextColor = OnBackground,
            unfocusedTextColor = OnBackground
        )
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChipSelector(
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit
) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { option ->
            FilterChip(
                selected = option == selected,
                onClick = { onSelected(option) },
                label = { Text(option) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Amber.copy(alpha = 0.2f),
                    selectedLabelColor = Amber,
                    containerColor = SurfaceVariant,
                    labelColor = OnSurfaceVariant
                ),
                border = FilterChipDefaults.filterChipBorder(
                    borderColor = Outline,
                    selectedBorderColor = Amber,
                    enabled = true, selected = option == selected
                )
            )
        }
    }
}
