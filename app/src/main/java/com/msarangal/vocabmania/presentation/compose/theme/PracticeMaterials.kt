package com.msarangal.vocabmania.presentation.compose.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * Phase 4 material color roles. Same action-vs-content hierarchy in light and dark —
 * filled practice commitment vs quiet paper vs chrome-light utilities.
 */
@Immutable
data class PracticeHeroColors(
    val container: Color,
    val onContainer: Color,
    val meta: Color,
)

@Immutable
data class PaperContentColors(
    val container: Color,
    val outline: Color,
)

@Composable
fun practiceHeroColors(darkTheme: Boolean = isSystemInDarkTheme()): PracticeHeroColors =
    if (darkTheme) {
        PracticeHeroColors(
            container = PracticeHeroFillDark,
            onContainer = PracticeHeroOnFillDark,
            meta = PracticeHeroMetaDark,
        )
    } else {
        PracticeHeroColors(
            container = PracticeHeroFillLight,
            onContainer = PracticeHeroOnFillLight,
            meta = PracticeHeroMetaLight,
        )
    }

@Composable
fun paperContentColors(darkTheme: Boolean = isSystemInDarkTheme()): PaperContentColors =
    if (darkTheme) {
        PaperContentColors(
            container = PaperContentFillDark,
            outline = PaperContentOutlineDark,
        )
    } else {
        PaperContentColors(
            container = PaperContentFillLight,
            outline = PaperContentOutlineLight,
        )
    }
