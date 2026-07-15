package com.msarangal.vocabmania.shared.data.repository

import com.msarangal.vocabmania.shared.data.mapper.toDomain
import com.msarangal.vocabmania.shared.data.mapper.toDueWord
import com.msarangal.vocabmania.shared.db.VocabManiaDatabase
import com.msarangal.vocabmania.shared.domain.model.DifficultyLevel
import com.msarangal.vocabmania.shared.domain.model.DueWord
import com.msarangal.vocabmania.shared.domain.model.ReviewRating
import com.msarangal.vocabmania.shared.domain.model.ReviewSchedule
import com.msarangal.vocabmania.shared.domain.model.SessionSummary
import com.msarangal.vocabmania.shared.domain.model.UserSettings
import com.msarangal.vocabmania.shared.domain.model.Word
import com.msarangal.vocabmania.shared.domain.repository.MigrationRepository
import com.msarangal.vocabmania.shared.domain.repository.ReviewRepository
import com.msarangal.vocabmania.shared.domain.repository.UserSettingsRepository
import com.msarangal.vocabmania.shared.domain.repository.WordRepository
import com.msarangal.vocabmania.shared.domain.srs.SpacedRepetitionScheduler
import com.msarangal.vocabmania.shared.domain.srs.Sm2LiteScheduler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

private const val MILLIS_PER_DAY = 86_400_000L

class SqlDelightWordRepository(
    private val database: VocabManiaDatabase,
) : WordRepository {

    override suspend fun countWords(): Long = withContext(Dispatchers.IO) {
        database.wordQueries.countAll().executeAsOne()
    }

    override suspend fun countWords(level: DifficultyLevel): Long = withContext(Dispatchers.IO) {
        database.wordQueries.countByLevel(level.code).executeAsOne()
    }

    override suspend fun getFavorites(): List<Word> = withContext(Dispatchers.IO) {
        database.wordQueries.selectFavorites().executeAsList().map { it.toDomain() }
    }

    override suspend fun getWord(wordId: Long): Word? = withContext(Dispatchers.IO) {
        database.wordQueries.selectById(wordId).executeAsOneOrNull()?.toDomain()
    }

    override suspend fun setFavorite(wordId: Long, isFavorite: Boolean) = withContext(Dispatchers.IO) {
        database.wordQueries.updateFavorite(
            is_favorite = if (isFavorite) 1 else 0,
            id = wordId,
        )
    }

    override suspend fun insertWord(
        text: String,
        meaning: String,
        usageExample: String?,
        level: DifficultyLevel,
        isFavorite: Boolean,
        firebaseLevelAttempt: String?,
    ): Long = withContext(Dispatchers.IO) {
        database.wordQueries.insertWord(
            text = text,
            meaning = meaning,
            usage_example = usageExample,
            level = level.code,
            is_favorite = if (isFavorite) 1 else 0,
            firebase_level_attempt = firebaseLevelAttempt,
        )
        database.wordQueries.selectIdByTextAndLevel(text, level.code).executeAsOne()
    }
}

class SqlDelightReviewRepository(
    private val database: VocabManiaDatabase,
    private val scheduler: SpacedRepetitionScheduler = Sm2LiteScheduler(),
) : ReviewRepository {

    override suspend fun getDueWords(
        level: DifficultyLevel,
        nowEpochMillis: Long,
        limit: Int,
        favoritesOnly: Boolean,
    ): List<DueWord> = withContext(Dispatchers.IO) {
        database.reviewCardQueries
            .selectDue(
                nowEpochMillis = nowEpochMillis,
                level = level.code,
                favoritesOnlyFilter = if (favoritesOnly) 1L else 0L,
                limit = limit.toLong(),
            )
            .executeAsList()
            .map { it.toDueWord() }
    }

    override suspend fun countDueWords(
        level: DifficultyLevel,
        nowEpochMillis: Long,
        favoritesOnly: Boolean,
    ): Long = withContext(Dispatchers.IO) {
        database.reviewCardQueries
            .countDue(
                nowEpochMillis = nowEpochMillis,
                level = level.code,
                favoritesOnlyFilter = if (favoritesOnly) 1L else 0L,
            )
            .executeAsOne()
    }

    override suspend fun applyRating(
        wordId: Long,
        rating: ReviewRating,
        nowEpochMillis: Long,
    ): ReviewSchedule = withContext(Dispatchers.IO) {
        val existing = database.reviewCardQueries.selectByWordId(wordId).executeAsOneOrNull()
        val currentCount = existing?.review_count?.toInt() ?: 0
        val currentInterval = existing?.interval_days ?: 0.0
        val currentEase = existing?.ease_factor ?: Sm2LiteScheduler.DEFAULT_EASE_FACTOR
        val schedule = scheduler.schedule(
            rating = rating,
            nowEpochMillis = nowEpochMillis,
            currentIntervalDays = currentInterval,
            currentEaseFactor = currentEase,
            reviewCount = currentCount,
        )

        database.reviewCardQueries.insertReviewCard(
            word_id = wordId,
            next_review_at = schedule.nextReviewAtEpochMillis,
            interval_days = schedule.intervalDays,
            last_reviewed_at = nowEpochMillis,
            review_count = schedule.reviewCount.toLong(),
            ease_factor = schedule.easeFactor,
        )
        schedule
    }

    override suspend fun ensureReviewCard(wordId: Long, nowEpochMillis: Long) = withContext(Dispatchers.IO) {
        val existing = database.reviewCardQueries.selectByWordId(wordId).executeAsOneOrNull()
        if (existing == null) {
            database.reviewCardQueries.insertReviewCard(
                word_id = wordId,
                next_review_at = nowEpochMillis,
                interval_days = 0.0,
                last_reviewed_at = null,
                review_count = 0,
                ease_factor = Sm2LiteScheduler.DEFAULT_EASE_FACTOR,
            )
        }
    }
}

class SqlDelightUserSettingsRepository(
    private val database: VocabManiaDatabase,
) : UserSettingsRepository {

    override suspend fun getSettings(): UserSettings = withContext(Dispatchers.IO) {
        database.userSettingsQueries.insertDefaultSettings()
        database.userSettingsQueries.selectSettings().executeAsOne().toDomain()
    }

    override suspend fun saveSettings(settings: UserSettings) = withContext(Dispatchers.IO) {
        database.userSettingsQueries.insertDefaultSettings()
        database.userSettingsQueries.updateSettings(
            onboarding_complete = if (settings.onboardingComplete) 1 else 0,
            selected_level = settings.selectedLevel.code,
            daily_goal = settings.dailyGoal.toLong(),
            current_streak = settings.currentStreak.toLong(),
            longest_streak = settings.longestStreak.toLong(),
            last_session_epoch_day = settings.lastSessionEpochDay,
            daily_reminder_enabled = if (settings.dailyReminderEnabled) 1 else 0,
        )
    }

    override suspend fun completeSession(
        nowEpochMillis: Long,
        reviewedCount: Int,
    ): SessionSummary = withContext(Dispatchers.IO) {
        val settings = getSettings()
        val today = nowEpochMillis / MILLIS_PER_DAY
        val updatedStreak = calculateStreak(settings, today)

        database.userSettingsQueries.updateStreak(
            current_streak = updatedStreak.currentStreak.toLong(),
            longest_streak = updatedStreak.longestStreak.toLong(),
            last_session_epoch_day = today,
        )

        SessionSummary(
            reviewedCount = reviewedCount,
            currentStreak = updatedStreak.currentStreak,
            longestStreak = updatedStreak.longestStreak,
        )
    }

    private fun calculateStreak(settings: UserSettings, today: Long): UserSettings {
        val lastDay = settings.lastSessionEpochDay
        if (lastDay == null) {
            return settings.copy(currentStreak = 1, longestStreak = maxOf(1, settings.longestStreak))
        }
        if (lastDay == today) {
            return settings
        }

        val nextStreak = if (lastDay == today - 1) settings.currentStreak + 1 else 1
        return settings.copy(
            currentStreak = nextStreak,
            longestStreak = maxOf(nextStreak, settings.longestStreak),
        )
    }
}

class SqlDelightMigrationRepository(
    private val database: VocabManiaDatabase,
) : MigrationRepository {

    override suspend fun isMigrationComplete(key: String): Boolean = withContext(Dispatchers.IO) {
        database.migrationStateQueries.isCompleted(key).executeAsOneOrNull() != null
    }

    override suspend fun markMigrationComplete(key: String, completedAtEpochMillis: Long) =
        withContext(Dispatchers.IO) {
            database.migrationStateQueries.markCompleted(key, completedAtEpochMillis)
        }
}
