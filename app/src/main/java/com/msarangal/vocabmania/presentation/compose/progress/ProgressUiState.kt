package com.msarangal.vocabmania.presentation.compose.progress

import com.msarangal.vocabmania.shared.domain.model.DifficultyLevel

data class LevelProgressUi(
    val masteryPercent: Int = 0,
    val matureCount: Int = 0,
    val reviewedCount: Int = 0,
    val activityLast7Days: Int = 0,
    val dailyActivity: List<Int> = List(7) { 0 },
)

data class ProgressUiState(
    val isLoading: Boolean = true,
    val selectedLevel: DifficultyLevel = DifficultyLevel.EASY,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val levelProgress: Map<DifficultyLevel, LevelProgressUi> = emptyMap(),
    val errorMessage: String? = null,
)
