package com.msarangal.vocabmania.shared.domain.repository

import com.msarangal.vocabmania.shared.domain.model.WordOfTheDay

interface WordOfTheDayRepository {
    /**
     * Returns a fresh word of the day when the network fetch succeeds,
     * otherwise the last cached value, or null when nothing is available.
     */
    suspend fun getWordOfTheDay(nowEpochMillis: Long): WordOfTheDay?
}
