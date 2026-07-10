package com.msarangal.vocabmania.shared.domain.srs

import com.msarangal.vocabmania.shared.domain.model.ReviewRating
import com.msarangal.vocabmania.shared.domain.model.ReviewSchedule
import kotlin.math.max
import kotlin.math.roundToLong

/**
 * SM-2 lite scheduler (Phase 2).
 * Maps ReviewRating → SM-2 quality: Again=1, Hard=3, Good=4, Easy=5.
 */
class Sm2LiteScheduler : SpacedRepetitionScheduler {

    override fun schedule(
        rating: ReviewRating,
        nowEpochMillis: Long,
        currentIntervalDays: Double,
        currentEaseFactor: Double,
        reviewCount: Int,
    ): ReviewSchedule {
        val quality = rating.toSm2Quality()
        var easeFactor = currentEaseFactor.coerceAtLeast(MIN_EASE_FACTOR)
        val intervalDays: Double
        val newReviewCount: Int

        if (quality < LAPSE_QUALITY_THRESHOLD) {
            newReviewCount = 0
            intervalDays = LAPSE_INTERVAL_DAYS
        } else {
            intervalDays = when (reviewCount) {
                0 -> firstReviewInterval(rating)
                1 -> SECOND_INTERVAL_DAYS
                else -> max(1.0, (currentIntervalDays * easeFactor).roundToLong().toDouble())
            }
            newReviewCount = reviewCount + 1
        }

        easeFactor = updateEaseFactor(easeFactor, quality)

        val nextReviewAt = nowEpochMillis + (intervalDays * MILLIS_PER_DAY).toLong()

        return ReviewSchedule(
            nextReviewAtEpochMillis = nextReviewAt,
            intervalDays = intervalDays,
            reviewCount = newReviewCount,
            easeFactor = easeFactor,
        )
    }

    companion object {
        const val DEFAULT_EASE_FACTOR = 2.5
        const val MIN_EASE_FACTOR = 1.3
        const val LAPSE_QUALITY_THRESHOLD = 3
        const val LAPSE_INTERVAL_DAYS = 0.0
        const val FIRST_HARD_INTERVAL_DAYS = 0.5
        const val FIRST_GOOD_INTERVAL_DAYS = 1.0
        const val FIRST_EASY_INTERVAL_DAYS = 3.0
        const val SECOND_INTERVAL_DAYS = 6.0
        private const val MILLIS_PER_DAY = 86_400_000L

        fun firstReviewInterval(rating: ReviewRating): Double = when (rating) {
            ReviewRating.HARD -> FIRST_HARD_INTERVAL_DAYS
            ReviewRating.GOOD -> FIRST_GOOD_INTERVAL_DAYS
            ReviewRating.EASY -> FIRST_EASY_INTERVAL_DAYS
            ReviewRating.AGAIN -> error("Lapse ratings use separate scheduling")
        }

        fun ReviewRating.toSm2Quality(): Int = when (this) {
            ReviewRating.AGAIN -> 1
            ReviewRating.HARD -> 3
            ReviewRating.GOOD -> 4
            ReviewRating.EASY -> 5
        }

        fun updateEaseFactor(currentEaseFactor: Double, quality: Int): Double {
            val delta = 0.1 - (5 - quality) * (0.08 + (5 - quality) * 0.02)
            return max(MIN_EASE_FACTOR, currentEaseFactor + delta)
        }
    }
}
