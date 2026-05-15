package com.mobiapp.ui.reminder

import android.app.TimePickerDialog
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mobiapp.data.entity.ReminderEntity
import com.mobiapp.ui.components.*
import com.mobiapp.ui.process.ChipSelector
import com.mobiapp.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

private val CATEGORIES = listOf("General", "Work", "Personal", "Health", "CCTV", "Access Control", "Network")
private val DAYS = listOf("Mon" to "1", "Tue" to "2", "Wed" to "3", "Thu" to "4", "Fri" to "5", "Sat" to "6", "Sun" to "7")

@Composable
fun AddEditReminderScreen(
    reminderId: Long?,
    onBack: () -> Unit,
    viewModel: ReminderViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var message by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(CATEGORIES.first()) }
    var repeatMode by remember { mutableStateOf("once") }
    var selectedDays by remember { mutableStateOf(setOf<String>()) }
    var timeMillis by remember { mutableStateOf(getDefaultTimeMillis()) }
    var isEdit by remember { mutableStateOf(false) }

    LaunchedEffect(reminderId) {
        reminderId?.let {
            val r = viewModel.getById(it) ?: return@let
            message = r.message; category = r.category; repeatMode = r.repeatMode
            timeMillis = r.timeMillis; isEdit = true
            selectedDays = r.weekdays.split(",").filter { d -> d.isNotBlank() }.toSet()
        }
    }

    val timeFmt = SimpleDateFormat("HH:mm", Locale.getDefault())
    val timeDisplay = timeFmt.format(Date(timeMillis))

    Scaffold(
        containerColor = Background,
        topBar = { MobiTopBar(if (isEdit) "Edit Reminder" else "New Reminder", onBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding)
                .verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MobiTextField(value = message, onValueChange = { message = it }, label = "Reminder Message", minLines = 2)

            // Time picker
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Time", color = OnSurfaceVariant, style = MaterialTheme.typography.labelMedium)
                OutlinedButton(
                    onClick = {
                        val cal = Calendar.getInstance().apply { timeInMillis = timeMillis }
                        TimePickerDialog(context, { _, h, m ->
                            val c = Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, h); set(Calendar.MINUTE, m); set(Calendar.SECOND, 0) }
                            timeMillis = c.timeInMillis % (24 * 60 * 60 * 1000L) + (h * 3600000L + m * 60000L)
                        }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
                    },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Amber),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Outline)
                ) {
                    Icon(Icons.Default.Schedule, null)
                    Spacer(Modifier.width(8.dp))
                    Text(timeDisplay, fontWeight = FontWeight.Bold)
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Repeat", color = OnSurfaceVariant, style = MaterialTheme.typography.labelMedium)
                ChipSelector(listOf("once", "daily", "weekdays"), repeatMode) { repeatMode = it }
            }

            if (repeatMode == "weekdays") {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Days", color = OnSurfaceVariant, style = MaterialTheme.typography.labelMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        DAYS.forEach { (label, value) ->
                            FilterChip(
                                selected = value in selectedDays,
                                onClick = {
                                    selectedDays = if (value in selectedDays) selectedDays - value else selectedDays + value
                                },
                                label = { Text(label) },
                                modifier = Modifier.weight(1f),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Amber.copy(alpha = 0.2f),
                                    selectedLabelColor = Amber,
                                    containerColor = SurfaceVariant,
                                    labelColor = OnSurfaceVariant
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    borderColor = Outline, selectedBorderColor = Amber, enabled = true, selected = value in selectedDays
                                )
                            )
                        }
                    }
                }
            }

            Text("Category", color = OnSurfaceVariant, style = MaterialTheme.typography.labelMedium)
            ChipSelector(CATEGORIES, category) { category = it }

            Button(
                onClick = {
                    if (message.isBlank()) return@Button
                    val reminder = ReminderEntity(
                        id = reminderId ?: 0L,
                        message = message.trim(),
                        category = category,
                        timeMillis = timeMillis,
                        repeatMode = repeatMode,
                        weekdays = selectedDays.joinToString(",")
                    )
                    viewModel.save(context, reminder)
                    onBack()
                },
                enabled = message.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Amber, contentColor = OnAmber)
            ) { Text(if (isEdit) "Save Changes" else "Schedule Reminder") }
        }
    }
}

private fun getDefaultTimeMillis(): Long {
    val cal = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 9); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0)
    }
    return cal.get(Calendar.HOUR_OF_DAY) * 3600000L + cal.get(Calendar.MINUTE) * 60000L
}
