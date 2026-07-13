package com.msarangal.vocabmania.shared.data.repository

import com.msarangal.vocabmania.shared.db.VocabManiaDatabase
import com.msarangal.vocabmania.shared.domain.model.WordOfTheDay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class SqlDelightWordOfTheDayCache(
    private val database: VocabManiaDatabase,
) {
    suspend fun getCached(): WordOfTheDay? = withContext(Dispatchers.IO) {
        database.wordOfTheDayQueries.selectCached().executeAsOneOrNull()?.let { row ->
            WordOfTheDay(
                word = row.word,
                meaning = row.meaning,
                usageExample = row.usage_example,
                fetchedAtEpochMillis = row.fetched_at,
                isFromCache = true,
            )
        }
    }

    suspend fun save(
        word: String,
        meaning: String,
        usageExample: String?,
        fetchedAtEpochMillis: Long,
    ) = withContext(Dispatchers.IO) {
        database.wordOfTheDayQueries.upsertCached(
            word = word,
            meaning = meaning,
            usage_example = usageExample,
            fetched_at = fetchedAtEpochMillis,
        )
    }
}
