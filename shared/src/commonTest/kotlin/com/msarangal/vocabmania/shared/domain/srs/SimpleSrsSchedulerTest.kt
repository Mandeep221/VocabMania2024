package com.msarangal.vocabmania.shared.domain.srs

import com.msarangal.vocabmania.shared.domain.model.ReviewRating
import kotlin.test.Test
import kotlin.test.assertEquals

class SimpleSrsSchedulerTest {

    private val scheduler = SimpleSrsScheduler()
    private val now = 1_700_000_000_000L

    @Test
    fun againSchedulesImmediately() {
        val result = scheduler.schedule(ReviewRating.AGAIN, now, currentReviewCount = 2)
        assertEquals(now, result.nextReviewAtEpochMillis)
        assertEquals(0.0, result.intervalDays)
        assertEquals(3, result.reviewCount)
    }

    @Test
    fun hardSchedulesInTwelveHours() {
        val result = scheduler.schedule(ReviewRating.HARD, now, currentReviewCount = 0)
        assertEquals(now + 43_200_000L, result.nextReviewAtEpochMillis)
        assertEquals(SimpleSrsScheduler.HARD_INTERVAL_DAYS, result.intervalDays)
        assertEquals(1, result.reviewCount)
    }

    @Test
    fun goodSchedulesInOneDay() {
        val result = scheduler.schedule(ReviewRating.GOOD, now, currentReviewCount = 4)
        assertEquals(now + 86_400_000L, result.nextReviewAtEpochMillis)
        assertEquals(SimpleSrsScheduler.GOOD_INTERVAL_DAYS, result.intervalDays)
        assertEquals(5, result.reviewCount)
    }

    @Test
    fun easySchedulesInThreeDays() {
        val result = scheduler.schedule(ReviewRating.EASY, now, currentReviewCount = 1)
        assertEquals(now + 259_200_000L, result.nextReviewAtEpochMillis)
        assertEquals(SimpleSrsScheduler.EASY_INTERVAL_DAYS, result.intervalDays)
        assertEquals(2, result.reviewCount)
    }
}
