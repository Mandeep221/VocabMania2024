package com.msarangal.vocabmania.shared.data.wotd

import com.msarangal.vocabmania.shared.data.firebase.FirebaseWordOfTheDayFetcher
import com.msarangal.vocabmania.shared.data.repository.SqlDelightWordOfTheDayCache
import com.msarangal.vocabmania.shared.domain.model.WordOfTheDay
import com.msarangal.vocabmania.shared.domain.repository.WordOfTheDayRepository

class FirebaseWordOfTheDayRepository(
    private val fetcher: FirebaseWordOfTheDayFetcher,
    private val cache: SqlDelightWordOfTheDayCache,
) : WordOfTheDayRepository {

    override suspend fun getWordOfTheDay(nowEpochMillis: Long): WordOfTheDay? {
        try {
            val fetched = fetcher.fetch()
            if (fetched != null) {
                cache.save(
                    word = fetched.word,
                    meaning = fetched.meaning,
                    usageExample = fetched.usageExample,
                    fetchedAtEpochMillis = nowEpochMillis,
                )
                return WordOfTheDay(
                    word = fetched.word,
                    meaning = fetched.meaning,
                    usageExample = fetched.usageExample,
                    fetchedAtEpochMillis = nowEpochMillis,
                    isFromCache = false,
                )
            }
        } catch (_: Exception) {
            // Fall through to cache.
        }
        return cache.getCached()
    }
}
