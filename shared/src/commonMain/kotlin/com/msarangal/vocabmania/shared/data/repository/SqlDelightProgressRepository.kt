package com.msarangal.vocabmania.shared.data.repository

import com.msarangal.vocabmania.shared.db.VocabManiaDatabase
import com.msarangal.vocabmania.shared.domain.model.DifficultyLevel
import com.msarangal.vocabmania.shared.domain.progress.MATURE_INTERVAL_DAYS
import com.msarangal.vocabmania.shared.domain.repository.ProgressRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class SqlDelightProgressRepository(
    private val database: VocabManiaDatabase,
) : ProgressRepository {

    override suspend fun countReviewedCards(level: DifficultyLevel): Long = withContext(Dispatchers.IO) {
        database.reviewCardQueries.countReviewedByLevel(level.code).executeAsOne()
    }

    override suspend fun countMatureCards(level: DifficultyLevel): Long = withContext(Dispatchers.IO) {
        database.reviewCardQueries.countMatureByLevel(level.code, MATURE_INTERVAL_DAYS).executeAsOne()
    }

    override suspend fun countActivitySince(
        level: DifficultyLevel,
        sinceEpochMillis: Long,
    ): Long = withContext(Dispatchers.IO) {
        database.reviewCardQueries.countActivitySince(level.code, sinceEpochMillis).executeAsOne()
    }

    override suspend fun countReviewedOnDay(
        level: DifficultyLevel,
        dayStartEpochMillis: Long,
        dayEndEpochMillis: Long,
    ): Long = withContext(Dispatchers.IO) {
        database.reviewCardQueries.countReviewedOnDay(
            level = level.code,
            last_reviewed_at = dayStartEpochMillis,
            last_reviewed_at_ = dayEndEpochMillis,
        ).executeAsOne()
    }
}
