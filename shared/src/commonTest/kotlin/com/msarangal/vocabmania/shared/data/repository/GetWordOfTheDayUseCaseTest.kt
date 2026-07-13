package com.msarangal.vocabmania.shared.data.repository

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.msarangal.vocabmania.shared.db.VocabManiaDatabase
import com.msarangal.vocabmania.shared.domain.model.WordOfTheDay
import com.msarangal.vocabmania.shared.domain.repository.WordOfTheDayRepository
import com.msarangal.vocabmania.shared.domain.usecase.GetWordOfTheDayUseCase
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GetWordOfTheDayUseCaseTest {

    @Test
    fun returnsNullWhenCacheEmptyAndFetchFails() = runBlocking {
        val cache = createCache()
        val repository = FakeWordOfTheDayRepository(
            cache = cache,
            fetchResult = null,
            shouldFail = true,
        )
        val useCase = GetWordOfTheDayUseCase(repository)

        assertNull(useCase(1_700_000_000_000L))
    }

    @Test
    fun returnsCachedWhenFetchFails() = runBlocking {
        val cache = createCache()
        cache.save("cached", "old meaning", "usage", 1_600_000_000_000L)
        val repository = FakeWordOfTheDayRepository(
            cache = cache,
            fetchResult = null,
            shouldFail = true,
        )
        val useCase = GetWordOfTheDayUseCase(repository)

        val result = useCase(1_700_000_000_000L)
        assertNotNull(result)
        assertEquals("cached", result.word)
        assertTrue(result.isFromCache)
    }

    @Test
    fun cachesAndReturnsFreshFetch() = runBlocking {
        val cache = createCache()
        val repository = FakeWordOfTheDayRepository(
            cache = cache,
            fetchResult = WordOfTheDay(
                word = "ephemeral",
                meaning = "lasting a very short time",
                usageExample = "an ephemeral moment",
                fetchedAtEpochMillis = 0L,
                isFromCache = false,
            ),
            shouldFail = false,
        )
        val useCase = GetWordOfTheDayUseCase(repository)
        val now = 1_700_000_000_000L

        val result = useCase(now)
        assertNotNull(result)
        assertEquals("ephemeral", result.word)
        assertEquals(false, result.isFromCache)

        val cached = cache.getCached()
        assertNotNull(cached)
        assertEquals("ephemeral", cached.word)
        assertEquals(now, cached.fetchedAtEpochMillis)
    }

    private fun createCache(): SqlDelightWordOfTheDayCache {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        VocabManiaDatabase.Schema.create(driver)
        return SqlDelightWordOfTheDayCache(VocabManiaDatabase(driver))
    }

    private class FakeWordOfTheDayRepository(
        private val cache: SqlDelightWordOfTheDayCache,
        private val fetchResult: WordOfTheDay?,
        private val shouldFail: Boolean,
    ) : WordOfTheDayRepository {
        override suspend fun getWordOfTheDay(nowEpochMillis: Long): WordOfTheDay? {
            if (shouldFail) return cache.getCached()
            val fetched = fetchResult ?: return cache.getCached()
            cache.save(
                word = fetched.word,
                meaning = fetched.meaning,
                usageExample = fetched.usageExample,
                fetchedAtEpochMillis = nowEpochMillis,
            )
            return fetched.copy(
                fetchedAtEpochMillis = nowEpochMillis,
                isFromCache = false,
            )
        }
    }
}
