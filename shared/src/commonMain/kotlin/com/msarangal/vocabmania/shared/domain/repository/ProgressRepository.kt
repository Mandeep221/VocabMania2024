package com.msarangal.vocabmania.shared.domain.repository

import com.msarangal.vocabmania.shared.domain.model.DailyActivity
import com.msarangal.vocabmania.shared.domain.model.DifficultyLevel

interface ProgressRepository {
    suspend fun countReviewedCards(level: DifficultyLevel): Long
    suspend fun countMatureCards(level: DifficultyLevel): Long
    suspend fun countActivitySince(level: DifficultyLevel, sinceEpochMillis: Long): Long
    suspend fun countReviewedOnDay(
        level: DifficultyLevel,
        dayStartEpochMillis: Long,
        dayEndEpochMillis: Long,
    ): Long
}
