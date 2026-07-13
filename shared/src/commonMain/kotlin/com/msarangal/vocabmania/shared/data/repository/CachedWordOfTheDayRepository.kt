package com.msarangal.vocabmania.shared.data.repository

import com.msarangal.vocabmania.shared.domain.model.WordOfTheDay
import com.msarangal.vocabmania.shared.domain.repository.WordOfTheDayRepository

/**
 * Cache-only / no-network fallback used by common tests and non-Android create paths.
 */
class CachedWordOfTheDayRepository(
    private val cache: SqlDelightWordOfTheDayCache,
) : WordOfTheDayRepository {
    override suspend fun getWordOfTheDay(nowEpochMillis: Long): WordOfTheDay? = cache.getCached()
}
