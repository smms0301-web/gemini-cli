package com.mobiapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val MobiTypography = Typography(
    headlineLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 28.sp, color = OnBackground),
    headlineMedium = TextStyle(fontWeight = FontWeight.Bold, fontSize = 22.sp, color = OnBackground),
    headlineSmall = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 18.sp, color = OnBackground),
    titleLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 18.sp, color = OnBackground),
    titleMedium = TextStyle(fontWeight = FontWeight.Medium, fontSize = 15.sp, color = OnBackground),
    bodyLarge = TextStyle(fontWeight = FontWeight.Normal, fontSize = 15.sp, color = OnSurface),
    bodyMedium = TextStyle(fontWeight = FontWeight.Normal, fontSize = 13.sp, color = OnSurface),
    labelLarge = TextStyle(fontWeight = FontWeight.Medium, fontSize = 13.sp, color = OnSurface),
    labelMedium = TextStyle(fontWeight = FontWeight.Normal, fontSize = 11.sp, color = OnSurface)
)
