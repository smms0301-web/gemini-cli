package com.mobiapp.ui.reminder

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mobiapp.data.entity.ReminderEntity
import com.mobiapp.navigation.Screen
import com.mobiapp.ui.components.*
import com.mobiapp.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ReminderListScreen(
    onNavigate: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: ReminderViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val reminders by viewModel.reminders.collectAsStateWithLifecycle()
    var showDelete by remember { mutableStateOf<ReminderEntity?>(null) }

    Scaffold(
        containerColor = Background,
        topBar = { MobiTopBar("Smart Reminders", onBack = onBack) },
        floatingActionButton = {
            MobiFab(onClick = { onNavigate(Screen.AddEditReminder.createRoute()) })
        }
    ) { padding ->
        if (reminders.isEmpty()) {
            EmptyState("No reminders. Tap + to schedule one.", Icons.Default.Notifications)
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(reminders, key = { it.id }) { reminder ->
                    ReminderCard(
                        reminder = reminder,
                        onClick = { onNavigate(Screen.AddEditReminder.createRoute(reminder.id)) },
                        onToggle = { viewModel.toggleEnabled(context, reminder) },
                        onDelete = { showDelete = reminder }
                    )
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }

    showDelete?.let { r ->
        DeleteDialog("Delete reminder?",
            onConfirm = { viewModel.delete(context, r); showDelete = null },
            onDismiss = { showDelete = null }
        )
    }
}

@Composable
private fun ReminderCard(
    reminder: ReminderEntity,
    onClick: () -> Unit,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    val timeFmt = SimpleDateFormat("HH:mm", Locale.getDefault())
    val timeStr = timeFmt.format(Date(reminder.timeMillis))
    val repeatStr = when (reminder.repeatMode) {
        "daily" -> "Daily"
        "weekdays" -> "Custom days"
        else -> "Once"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceVariant)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(reminder.message, color = if (reminder.isEnabled) OnBackground else OnSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, maxLines = 2)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(timeStr, color = Amber, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                TagChip(repeatStr)
                TagChip(reminder.category, color = AmberLight)
            }
        }
        Switch(
            checked = reminder.isEnabled,
            onCheckedChange = { onToggle() },
            colors = SwitchDefaults.colors(checkedThumbColor = OnAmber, checkedTrackColor = Amber, uncheckedTrackColor = SurfaceContainer)
        )
        IconButton(onClick = onDelete) {
            Icon(Icons.Default.DeleteOutline, "Delete", tint = OnSurfaceVariant)
        }
    }
}
