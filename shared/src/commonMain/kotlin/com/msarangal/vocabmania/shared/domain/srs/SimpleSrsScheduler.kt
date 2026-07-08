package com.msarangal.vocabmania.shared.domain.srs

import com.msarangal.vocabmania.shared.domain.model.ReviewRating
import com.msarangal.vocabmania.shared.domain.model.ReviewSchedule

/**
 * v1 SRS scheduler (weeks 1–4).
 * Again → due immediately, Hard → 12 hours, Good → 1 day, Easy → 3 days.
 */
class SimpleSrsScheduler {

    fun schedule(
        rating: ReviewRating,
        nowEpochMillis: Long,
        currentReviewCount: Int,
    ): ReviewSchedule {
        val intervalDays = when (rating) {
            ReviewRating.AGAIN -> 0.0
            ReviewRating.HARD -> HARD_INTERVAL_DAYS
            ReviewRating.GOOD -> GOOD_INTERVAL_DAYS
            ReviewRating.EASY -> EASY_INTERVAL_DAYS
        }

        val nextReviewAt = nowEpochMillis + (intervalDays * MILLIS_PER_DAY).toLong()

        return ReviewSchedule(
            nextReviewAtEpochMillis = nextReviewAt,
            intervalDays = intervalDays,
            reviewCount = currentReviewCount + 1,
        )
    }

    companion object {
        const val HARD_INTERVAL_DAYS = 0.5
        const val GOOD_INTERVAL_DAYS = 1.0
        const val EASY_INTERVAL_DAYS = 3.0
        private const val MILLIS_PER_DAY = 86_400_000L
    }
}
