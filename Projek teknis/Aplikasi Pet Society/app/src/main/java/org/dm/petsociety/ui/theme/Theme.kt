// File: Theme.kt
package org.dm.petsociety.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = AccentTeal,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = PrimaryDarkNavy,
    surface = PrimaryDarkNavy,
    onBackground = Color.White,
    onSurface = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = AccentTeal,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black,
)

@Composable
fun PetSocietyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window

            // PERBAIKAN: Gunakan status bar color dari ColorScheme background
            window.statusBarColor = colorScheme.background.toArgb()

            // Gunakan WindowCompat untuk mengatur ikon status bar (Light/Dark mode)
            // Di sini kita mengatur ikon agar terlihat terang di atas latar belakang PrimaryDarkNavy (gelap)
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false // false = ikon gelap
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}