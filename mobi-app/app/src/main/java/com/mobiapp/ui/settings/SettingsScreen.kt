package com.mobiapp.ui.settings

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricManager
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mobiapp.ui.components.*
import com.mobiapp.ui.theme.*
import com.mobiapp.util.BackupManager
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var biometricEnabled by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var vibrationEnabled by remember { mutableStateOf(true) }
    var isExporting by remember { mutableStateOf(false) }

    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let {
            scope.launch {
                val success = BackupManager.importBackup(context, it)
                Toast.makeText(context, if (success) "Backup restored. Restart app." else "Restore failed.", Toast.LENGTH_LONG).show()
            }
        }
    }

    Scaffold(
        containerColor = Background,
        topBar = { MobiTopBar("Settings", onBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding)
                .verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SettingsSectionHeader("Security")
            SettingsToggleRow(
                icon = Icons.Default.Fingerprint,
                title = "Biometric Lock",
                subtitle = "Require fingerprint or face to open",
                checked = biometricEnabled,
                onChecked = { enabled ->
                    if (enabled) {
                        val bm = BiometricManager.from(context)
                        if (bm.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS) {
                            biometricEnabled = true
                        } else {
                            Toast.makeText(context, "Biometrics not available on this device", Toast.LENGTH_SHORT).show()
                        }
                    } else biometricEnabled = false
                }
            )

            SettingsSectionHeader("Notifications")
            SettingsToggleRow(
                icon = Icons.Default.Notifications,
                title = "Push Notifications",
                subtitle = "Enable reminder notifications",
                checked = notificationsEnabled,
                onChecked = { notificationsEnabled = it }
            )
            SettingsToggleRow(
                icon = Icons.Default.Vibration,
                title = "Vibration",
                subtitle = "Vibrate on reminder",
                checked = vibrationEnabled,
                onChecked = { vibrationEnabled = it }
            )

            SettingsSectionHeader("Data & Backup")
            SettingsActionRow(
                icon = Icons.Default.Upload,
                title = "Export Backup",
                subtitle = "Save all data to device storage",
                onClick = {
                    isExporting = true
                    scope.launch {
                        val file = BackupManager.exportBackup(context)
                        isExporting = false
                        if (file != null) {
                            Toast.makeText(context, "Backup saved to: ${file.absolutePath}", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(context, "Backup failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                loading = isExporting
            )
            SettingsActionRow(
                icon = Icons.Default.Download,
                title = "Restore Backup",
                subtitle = "Import data from a backup file",
                onClick = { importLauncher.launch(arrayOf("application/octet-stream", "*/*")) }
            )

            SettingsSectionHeader("About")
            MobiCard {
                Text("Mobi App", color = OnBackground, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Version 1.0", color = OnSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(4.dp))
                Text("Personal command center — fully offline, all data on device.", color = OnSurfaceVariant, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        title.uppercase(),
        color = Amber,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        letterSpacing = androidx.compose.ui.unit.TextUnit(2f, androidx.compose.ui.unit.TextUnitType.Sp),
        modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
    )
}

@Composable
private fun SettingsToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onChecked: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .background(SurfaceVariant, RoundedCornerShape(10.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = OnSurfaceVariant, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = OnBackground, style = MaterialTheme.typography.bodyMedium)
            Text(subtitle, color = OnSurfaceVariant, style = MaterialTheme.typography.bodySmall)
        }
        Switch(
            checked = checked, onCheckedChange = onChecked,
            colors = SwitchDefaults.colors(checkedThumbColor = OnAmber, checkedTrackColor = Amber, uncheckedTrackColor = SurfaceContainer)
        )
    }
}

@Composable
private fun SettingsActionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    loading: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .background(SurfaceVariant, RoundedCornerShape(10.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = OnSurfaceVariant, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = OnBackground, style = MaterialTheme.typography.bodyMedium)
            Text(subtitle, color = OnSurfaceVariant, style = MaterialTheme.typography.bodySmall)
        }
        if (loading) {
            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Amber, strokeWidth = 2.dp)
        } else {
            TextButton(onClick = onClick, colors = ButtonDefaults.textButtonColors(contentColor = Amber)) {
                Text("Go")
            }
        }
    }
}
