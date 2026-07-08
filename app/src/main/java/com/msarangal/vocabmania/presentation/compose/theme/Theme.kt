package com.msarangal.vocabmania.presentation.compose.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = VocabTeal,
    onPrimary = VocabOnPrimary,
    primaryContainer = VocabTealLight,
    onPrimaryContainer = VocabTealDark,
    secondary = VocabAccent,
    onSecondary = VocabOnPrimary,
    tertiary = VocabGreen,
    background = VocabBackground,
    onBackground = VocabOnBackground,
    surface = VocabSurface,
    onSurface = VocabOnBackground,
    onSurfaceVariant = VocabOnSurfaceVariant,
)

private val DarkColorScheme = darkColorScheme(
    primary = VocabTealLight,
    onPrimary = VocabTealDark,
    primaryContainer = VocabTeal,
    onPrimaryContainer = VocabOnPrimary,
    secondary = VocabAccent,
    tertiary = VocabGreen,
)

@Composable
fun VocabManiaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        content = content,
    )
}
