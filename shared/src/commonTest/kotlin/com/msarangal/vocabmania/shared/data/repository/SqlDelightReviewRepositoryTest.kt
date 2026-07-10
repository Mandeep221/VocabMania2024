package com.msarangal.vocabmania.shared.data.repository

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.msarangal.vocabmania.shared.db.VocabManiaDatabase
import com.msarangal.vocabmania.shared.domain.model.ReviewRating
import com.msarangal.vocabmania.shared.domain.srs.Sm2LiteScheduler
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SqlDelightReviewRepositoryTest {

    @Test
    fun applyRatingPersistsScheduleWithEaseFactor() = runBlocking {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        VocabManiaDatabase.Schema.create(driver)
        val database = VocabManiaDatabase(driver)
        val repository = SqlDelightReviewRepository(database)

        database.wordQueries.insertWord(
            text = "ephemeral",
            meaning = "lasting a very short time",
            usage_example = null,
            level = "E",
            is_favorite = 0,
            firebase_level_attempt = null,
        )
        val wordId = database.wordQueries.selectIdByTextAndLevel("ephemeral", "E").executeAsOne()
        val now = 1_700_000_000_000L
        repository.ensureReviewCard(wordId, now)

        val schedule = repository.applyRating(wordId, ReviewRating.EASY, now)

        assertEquals(3.0, schedule.intervalDays)
        assertEquals(1, schedule.reviewCount)
        assertTrue(schedule.easeFactor > Sm2LiteScheduler.DEFAULT_EASE_FACTOR)

        val stored = database.reviewCardQueries.selectByWordId(wordId).executeAsOne()
        assertEquals(schedule.nextReviewAtEpochMillis, stored.next_review_at)
        assertEquals(schedule.intervalDays, stored.interval_days)
        assertEquals(schedule.reviewCount.toLong(), stored.review_count)
        assertEquals(schedule.easeFactor, stored.ease_factor)
        assertEquals(now, stored.last_reviewed_at)
    }
}
