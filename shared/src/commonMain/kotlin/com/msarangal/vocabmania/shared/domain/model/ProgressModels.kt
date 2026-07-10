package com.msarangal.vocabmania.shared.domain.model

data class DailyActivity(
    val epochDay: Long,
    val reviewCount: Int,
)

data class LevelProgress(
    val level: DifficultyLevel,
    val masteryPercent: Int,
    val matureCount: Long,
    val reviewedCount: Long,
    val activityLast7Days: Long,
    val dailyActivity: List<DailyActivity>,
)

data class ProgressDashboard(
    val selectedLevel: DifficultyLevel,
    val currentStreak: Int,
    val longestStreak: Int,
    val levelProgress: Map<DifficultyLevel, LevelProgress>,
)
