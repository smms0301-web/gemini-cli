package com.mobiapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mobiapp.ui.theme.*

@Composable
fun MobiTopBar(
    title: String,
    onBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Surface)
            .statusBarsPadding()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onBack != null) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, "Back", tint = OnBackground)
            }
        } else {
            Spacer(Modifier.width(8.dp))
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = OnBackground,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f).padding(start = if (onBack != null) 0.dp else 8.dp)
        )
        Row(content = actions)
    }
    HorizontalDivider(color = Outline, thickness = 0.5.dp)
}

@Composable
fun MobiCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val mod = modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(12.dp))
        .background(SurfaceVariant)
        .border(0.5.dp, Outline, RoundedCornerShape(12.dp))
        .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
        .padding(16.dp)
    Column(modifier = mod, content = content)
}

@Composable
fun TagChip(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Amber,
    onClick: (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.15f))
            .border(0.5.dp, color.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(text, color = color, fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun MobiFab(onClick: () -> Unit, icon: ImageVector = Icons.Default.Add) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = Amber,
        contentColor = OnAmber,
        shape = CircleShape
    ) {
        Icon(icon, contentDescription = "Add")
    }
}

@Composable
fun EmptyState(message: String, icon: ImageVector = Icons.Default.Info) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(icon, null, tint = OnSurfaceVariant, modifier = Modifier.size(56.dp))
        Text(message, color = OnSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun DeleteDialog(
    title: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceVariant,
        title = { Text(title, color = OnBackground) },
        text = { Text("This action cannot be undone.", color = OnSurfaceVariant) },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("Delete", color = ErrorColor) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = Amber) }
        }
    )
}

@Composable
fun MobiSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String = "Search…",
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceVariant)
            .border(0.5.dp, Outline, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Search, null, tint = OnSurfaceVariant, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(8.dp))
        BasicTextField(
            query, onQueryChange, placeholder, Modifier.weight(1f)
        )
        if (query.isNotEmpty()) {
            IconButton(onClick = { onQueryChange("") }, modifier = Modifier.size(20.dp)) {
                Icon(Icons.Default.Clear, null, tint = OnSurfaceVariant, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
private fun BasicTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    androidx.compose.foundation.text.BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        textStyle = MaterialTheme.typography.bodyMedium.copy(color = OnBackground),
        singleLine = true,
        decorationBox = { inner ->
            if (value.isEmpty()) Text(placeholder, color = OnSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
            inner()
        }
    )
}
