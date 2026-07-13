package com.msarangal.vocabmania.shared.domain.repository

import com.msarangal.vocabmania.shared.domain.model.DifficultyLevel
import com.msarangal.vocabmania.shared.domain.model.DueWord
import com.msarangal.vocabmania.shared.domain.model.ReviewRating
import com.msarangal.vocabmania.shared.domain.model.ReviewSchedule
import com.msarangal.vocabmania.shared.domain.model.SessionSummary
import com.msarangal.vocabmania.shared.domain.model.UserSettings
import com.msarangal.vocabmania.shared.domain.model.Word

interface WordRepository {
    suspend fun countWords(): Long
    suspend fun countWords(level: DifficultyLevel): Long
    suspend fun getFavorites(): List<Word>
    suspend fun getWord(wordId: Long): Word?
    suspend fun setFavorite(wordId: Long, isFavorite: Boolean)
    suspend fun insertWord(
        text: String,
        meaning: String,
        usageExample: String?,
        level: DifficultyLevel,
        isFavorite: Boolean = false,
        firebaseLevelAttempt: String? = null,
    ): Long
}

interface ReviewRepository {
    suspend fun getDueWords(
        level: DifficultyLevel,
        nowEpochMillis: Long,
        limit: Int,
    ): List<DueWord>

    suspend fun countDueWords(
        level: DifficultyLevel,
        nowEpochMillis: Long,
    ): Long

    suspend fun applyRating(
        wordId: Long,
        rating: ReviewRating,
        nowEpochMillis: Long,
    ): ReviewSchedule

    suspend fun ensureReviewCard(
        wordId: Long,
        nowEpochMillis: Long,
    )
}

interface UserSettingsRepository {
    suspend fun getSettings(): UserSettings
    suspend fun saveSettings(settings: UserSettings)
    suspend fun completeSession(nowEpochMillis: Long, reviewedCount: Int): SessionSummary
}

interface MigrationRepository {
    suspend fun isMigrationComplete(key: String): Boolean
    suspend fun markMigrationComplete(key: String, completedAtEpochMillis: Long)
}
