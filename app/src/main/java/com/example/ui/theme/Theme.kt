package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
      primary = CalmGreenDark,
      primaryContainer = CalmGreenDarkContainer,
      onPrimaryContainer = CalmGreenOnDarkContainer,
      surface = CalmSurfaceDark,
      background = CalmSurfaceDark,
      onSurface = CalmOnSurfaceDark,
      onBackground = CalmOnSurfaceDark,
  )

private val LightColorScheme =
  lightColorScheme(
      primary = CalmGreenLight,
      primaryContainer = CalmGreenPrimaryContainer,
      onPrimaryContainer = CalmGreenOnPrimaryContainer,
      surface = CalmSurfaceLight,
      background = CalmSurfaceLight,
      onSurface = CalmOnSurfaceLight,
      onBackground = CalmOnSurfaceLight,
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Disabling dynamic colors to enforce calm palette
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
