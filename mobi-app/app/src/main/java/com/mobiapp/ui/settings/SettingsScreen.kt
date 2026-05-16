package com.mobiapp.ui.settings

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.mobiapp.ui.components.MobiTopBar
import com.mobiapp.ui.theme.Amber
import com.mobiapp.util.BackupManager

@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var biometricEnabled by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(true) }

    Scaffold(
        topBar = { MobiTopBar("Settings", onBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Security", style = MaterialTheme.typography.titleMedium, color = Amber)
            SettingsRow(
                label = "Biometric Lock",
                checked = biometricEnabled,
                onCheckedChange = { biometricEnabled = it }
            )

            Spacer(Modifier.height(8.dp))
            Text("Notifications", style = MaterialTheme.typography.titleMedium, color = Amber)
            SettingsRow(
                label = "Enable Reminders",
                checked = notificationsEnabled,
                onCheckedChange = { notificationsEnabled = it }
            )

            Spacer(Modifier.height(8.dp))
            Text("Data", style = MaterialTheme.typography.titleMedium, color = Amber)
            Button(
                onClick = {
                    val file = BackupManager.export(context)
                    val msg = if (file != null) "Backup saved to ${file.absolutePath}" else "Backup failed"
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Amber)
            ) {
                Text("Export Backup", color = MaterialTheme.colorScheme.onPrimary)
            }
            OutlinedButton(
                onClick = {
                    Toast.makeText(context, "Select a backup file to restore", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Import Backup")
            }
        }
    }
}

@Composable
private fun SettingsRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Amber,
                checkedTrackColor = Amber.copy(alpha = 0.4f)
            )
        )
    }
}
