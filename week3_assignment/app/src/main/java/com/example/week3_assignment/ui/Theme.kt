package com.example.week3_assignment.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

internal val Canvas = Color(0xFF0C1320)
internal val Panel = Color(0xFF152033)
internal val Ink = Color(0xFFE6EDF8)
internal val Muted = Color(0xFFA2AFC3)
internal val Accent = Color(0xFF6AA8FF)
internal val Line = Color(0xFF2B3950)
internal val Amber = Color(0xFFDBB378)

@Composable
fun SmartHomeTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Accent,
            onPrimary = Canvas,
            primaryContainer = Color(0xFF233B5D),
            onPrimaryContainer = Ink,
            secondary = Amber,
            onSecondary = Canvas,
            background = Canvas,
            onBackground = Ink,
            surface = Panel,
            onSurface = Ink,
            surfaceVariant = Color(0xFF1D2B40),
            onSurfaceVariant = Muted,
            outline = Muted,
            outlineVariant = Line,
        ),
        typography = Typography(
            headlineLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 26.sp, lineHeight = 32.sp, letterSpacing = (-0.5).sp),
            titleLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 21.sp, lineHeight = 28.sp),
            titleMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Medium, fontSize = 16.sp, lineHeight = 22.sp),
            bodyMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 14.sp, lineHeight = 20.sp),
            labelLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 20.sp),
        ),
        content = content,
    )
}
