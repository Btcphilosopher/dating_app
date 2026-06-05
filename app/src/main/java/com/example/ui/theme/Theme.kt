package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = SoftGold,
    secondary = LightGold,
    tertiary = GoldAccent,
    background = DeepNavy,
    surface = MutedNavy,
    onPrimary = DeepNavy,
    onSecondary = DeepNavy,
    onTertiary = DeepNavy,
    onBackground = Ivory,
    onSurface = Ivory,
    surfaceVariant = SteelBlue,
    onSurfaceVariant = WarmGrey
)

private val LightColorScheme = lightColorScheme(
    primary = DeepNavy,
    secondary = SoftGold,
    tertiary = DarkGold,
    background = WarmWhite,
    surface = Ivory,
    onPrimary = WarmWhite,
    onSecondary = Charcoal,
    onTertiary = WarmWhite,
    onBackground = Charcoal,
    onSurface = Charcoal,
    surfaceVariant = WarmGrey,
    onSurfaceVariant = Charcoal
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
