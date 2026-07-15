package com.msarangal.vocabmania.presentation.compose.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = DeepTeal,
    onPrimary = OnAccent,
    primaryContainer = TealMist,
    onPrimaryContainer = DeepTealDark,
    secondary = CoralAmber,
    onSecondary = OnAccent,
    secondaryContainer = CoralAmberSoft,
    onSecondaryContainer = InkBrown,
    tertiary = ScholarGreen,
    onTertiary = OnAccent,
    background = PaperCream,
    onBackground = InkBrown,
    surface = PaperSurface,
    onSurface = InkBrown,
    surfaceVariant = PaperMuted,
    onSurfaceVariant = InkMuted,
    outline = InkMuted.copy(alpha = 0.45f),
    error = ErrorRed,
    onError = OnAccent,
)

private val DarkColorScheme = darkColorScheme(
    primary = SoftTealDark,
    onPrimary = DeepTealDark,
    primaryContainer = DeepTealContainer,
    onPrimaryContainer = SoftTeal,
    secondary = CoralAmberDark,
    onSecondary = DuskBackground,
    secondaryContainer = Color(0xFF5C3A22),
    onSecondaryContainer = CoralAmberSoft,
    tertiary = ScholarGreenDark,
    onTertiary = DuskBackground,
    background = DuskBackground,
    onBackground = CreamText,
    surface = DuskSurface,
    onSurface = CreamText,
    surfaceVariant = DuskMuted,
    onSurfaceVariant = CreamMuted,
    outline = CreamMuted.copy(alpha = 0.5f),
    error = ErrorRedDark,
    onError = DuskBackground,
)

private val VocabShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(18.dp),
    extraLarge = RoundedCornerShape(24.dp),
)

@Composable
fun VocabManiaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = VocabTypography,
        shapes = VocabShapes,
        content = content,
    )
}
