package com.msarangal.vocabmania.shared.domain.model

enum class DifficultyLevel(val code: String) {
    EASY("E"),
    MEDIUM("M"),
    TOUGH("T");

    companion object {
        fun fromCode(code: String): DifficultyLevel =
            entries.firstOrNull { it.code.equals(code, ignoreCase = true) }
                ?: EASY
    }
}

enum class ReviewRating {
    AGAIN,
    HARD,
    GOOD,
    EASY,
}

data class Word(
    val id: Long,
    val text: String,
    val meaning: String,
    val usageExample: String?,
    val level: DifficultyLevel,
    val isFavorite: Boolean,
)

data class ReviewCard(
    val wordId: Long,
    val nextReviewAtEpochMillis: Long,
    val intervalDays: Double,
    val lastReviewedAtEpochMillis: Long?,
    val reviewCount: Int,
)

data class DueWord(
    val word: Word,
    val reviewCard: ReviewCard,
)

data class UserSettings(
    val onboardingComplete: Boolean,
    val selectedLevel: DifficultyLevel,
    val dailyGoal: Int,
    val currentStreak: Int,
    val longestStreak: Int,
    val lastSessionEpochDay: Long?,
)

data class ReviewSchedule(
    val nextReviewAtEpochMillis: Long,
    val intervalDays: Double,
    val reviewCount: Int,
)

data class SessionSummary(
    val reviewedCount: Int,
    val currentStreak: Int,
    val longestStreak: Int,
)
