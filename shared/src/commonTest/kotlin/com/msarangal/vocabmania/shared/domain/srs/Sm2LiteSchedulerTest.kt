package com.msarangal.vocabmania.shared.domain.srs

import com.msarangal.vocabmania.shared.domain.model.ReviewRating
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class Sm2LiteSchedulerTest {

    private val scheduler = Sm2LiteScheduler()
    private val now = 1_700_000_000_000L

    @Test
    fun firstGoodReviewSchedulesOneDay() {
        val result = scheduler.schedule(
            rating = ReviewRating.GOOD,
            nowEpochMillis = now,
            currentIntervalDays = 0.0,
            currentEaseFactor = Sm2LiteScheduler.DEFAULT_EASE_FACTOR,
            reviewCount = 0,
        )
        assertEquals(now + 86_400_000L, result.nextReviewAtEpochMillis)
        assertEquals(1.0, result.intervalDays)
        assertEquals(1, result.reviewCount)
        assertEquals(2.5, result.easeFactor)
    }

    @Test
    fun secondGoodReviewSchedulesSixDays() {
        val result = scheduler.schedule(
            rating = ReviewRating.GOOD,
            nowEpochMillis = now,
            currentIntervalDays = 1.0,
            currentEaseFactor = 2.5,
            reviewCount = 1,
        )
        assertEquals(6.0, result.intervalDays)
        assertEquals(2, result.reviewCount)
        assertEquals(now + 518_400_000L, result.nextReviewAtEpochMillis)
    }

    @Test
    fun thirdGoodReviewMultipliesIntervalByEaseFactor() {
        val result = scheduler.schedule(
            rating = ReviewRating.GOOD,
            nowEpochMillis = now,
            currentIntervalDays = 6.0,
            currentEaseFactor = 2.5,
            reviewCount = 2,
        )
        assertEquals(15.0, result.intervalDays)
        assertEquals(3, result.reviewCount)
    }

    @Test
    fun againSchedulesImmediately() {
        val result = scheduler.schedule(
            rating = ReviewRating.AGAIN,
            nowEpochMillis = now,
            currentIntervalDays = 15.0,
            currentEaseFactor = 2.5,
            reviewCount = 5,
        )
        assertEquals(0.0, result.intervalDays)
        assertEquals(now, result.nextReviewAtEpochMillis)
        assertEquals(0, result.reviewCount)
        assertTrue(result.easeFactor < 2.5)
    }

    @Test
    fun firstEasyReviewSchedulesThreeDays() {
        val result = scheduler.schedule(
            rating = ReviewRating.EASY,
            nowEpochMillis = now,
            currentIntervalDays = 0.0,
            currentEaseFactor = Sm2LiteScheduler.DEFAULT_EASE_FACTOR,
            reviewCount = 0,
        )
        assertEquals(3.0, result.intervalDays)
        assertEquals(now + 259_200_000L, result.nextReviewAtEpochMillis)
        assertEquals(1, result.reviewCount)
        assertTrue(result.easeFactor > 2.5)
    }

    @Test
    fun firstHardReviewSchedulesTwelveHours() {
        val result = scheduler.schedule(
            rating = ReviewRating.HARD,
            nowEpochMillis = now,
            currentIntervalDays = 0.0,
            currentEaseFactor = Sm2LiteScheduler.DEFAULT_EASE_FACTOR,
            reviewCount = 0,
        )
        assertEquals(0.5, result.intervalDays)
        assertEquals(now + 43_200_000L, result.nextReviewAtEpochMillis)
    }

    @Test
    fun easySchedulesLongerIntervalThanAgainOnNewWord() {
        val again = scheduler.schedule(
            rating = ReviewRating.AGAIN,
            nowEpochMillis = now,
            currentIntervalDays = 0.0,
            currentEaseFactor = 2.5,
            reviewCount = 0,
        )
        val easy = scheduler.schedule(
            rating = ReviewRating.EASY,
            nowEpochMillis = now,
            currentIntervalDays = 0.0,
            currentEaseFactor = 2.5,
            reviewCount = 0,
        )
        assertTrue(easy.intervalDays > again.intervalDays)
    }

    @Test
    fun easySchedulesLongerIntervalThanAgainOnMatureCard() {
        val again = scheduler.schedule(
            rating = ReviewRating.AGAIN,
            nowEpochMillis = now,
            currentIntervalDays = 6.0,
            currentEaseFactor = 2.5,
            reviewCount = 2,
        )
        val easy = scheduler.schedule(
            rating = ReviewRating.EASY,
            nowEpochMillis = now,
            currentIntervalDays = 6.0,
            currentEaseFactor = 2.5,
            reviewCount = 2,
        )
        assertTrue(easy.intervalDays > again.intervalDays)
    }

    @Test
    fun historyMigrationUsesDefaultEaseFactor() {
        assertEquals(2.5, Sm2HistoryMigration.initialEaseFactorForExistingCard())
    }
}
