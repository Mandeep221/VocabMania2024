package com.msarangal.vocabmania.shared.domain.srs

import com.msarangal.vocabmania.shared.domain.model.ReviewRating
import com.msarangal.vocabmania.shared.domain.model.ReviewSchedule

interface SpacedRepetitionScheduler {
    fun schedule(
        rating: ReviewRating,
        nowEpochMillis: Long,
        currentIntervalDays: Double,
        currentEaseFactor: Double,
        reviewCount: Int,
    ): ReviewSchedule
}
