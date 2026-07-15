package com.msarangal.vocabmania.presentation.compose.theme

import androidx.compose.ui.graphics.Color

// Warm-scholar light — cream/paper, deep teal, coral-amber accent
val PaperCream = Color(0xFFF7F1E8)
val PaperSurface = Color(0xFFFFFBF5)
val PaperMuted = Color(0xFFE9E1D4)
val InkBrown = Color(0xFF2C241C)
val InkMuted = Color(0xFF6B6056)

val DeepTeal = Color(0xFF0D5C63)
val DeepTealDark = Color(0xFF084048)
val SoftTeal = Color(0xFF7EC8C4)
val TealMist = Color(0xFFD4EDEC)

val CoralAmber = Color(0xFFE07A3D)
val CoralAmberSoft = Color(0xFFF3C6A4)
val ScholarGreen = Color(0xFF2F9E6E)

val ErrorRed = Color(0xFFB3261E)
val OnAccent = Color(0xFFFFFFFF)

// Warm-scholar dark — dusk ink, not cold gray
val DuskBackground = Color(0xFF1A1714)
val DuskSurface = Color(0xFF242019)
val DuskMuted = Color(0xFF3A342C)
val CreamText = Color(0xFFF2EDE4)
val CreamMuted = Color(0xFFB5A99A)

val SoftTealDark = Color(0xFF6DD0CA)
val DeepTealContainer = Color(0xFF0F4A50)
val CoralAmberDark = Color(0xFFF0A05A)
val ScholarGreenDark = Color(0xFF5FCF9A)
val ErrorRedDark = Color(0xFFF2B8B5)

// Phase 4 material roles — action (hero) vs content (paper) vs chrome (utility)
val PracticeHeroFillLight = DeepTeal
val PracticeHeroFillDark = Color(0xFF12727A)
val PracticeHeroOnFillLight = OnAccent
val PracticeHeroOnFillDark = CreamText
val PracticeHeroMetaLight = Color(0xFFB8E0DD)
val PracticeHeroMetaDark = SoftTeal.copy(alpha = 0.88f)

val PaperContentFillLight = PaperSurface
val PaperContentFillDark = Color(0xFF2C271F)
val PaperContentOutlineLight = PaperMuted
val PaperContentOutlineDark = DuskMuted

// Legacy aliases kept for any residual references
val VocabTeal = DeepTeal
val VocabTealDark = DeepTealDark
val VocabTealLight = SoftTeal
val VocabAccent = CoralAmber
val VocabGreen = ScholarGreen
val VocabBackground = PaperCream
val VocabSurface = PaperSurface
val VocabOnPrimary = OnAccent
val VocabOnBackground = InkBrown
val VocabOnSurfaceVariant = InkMuted
