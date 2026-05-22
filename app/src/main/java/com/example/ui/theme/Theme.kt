package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme =
  darkColorScheme(
      primary = AccentPrimary,
      surface = BgPrimary,
      surfaceVariant = BgSecondary,
      background = BgPrimary,
      onSurface = TextPrimary,
      onSurfaceVariant = TextSecondary,
      onBackground = TextPrimary,
      outline = BorderDefault
  )

@Composable
fun MyApplicationTheme(
  content: @Composable () -> Unit,
) {
  MaterialTheme(colorScheme = DarkColorScheme, typography = Typography, content = content)
}
