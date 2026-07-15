package com.msarangal.vocabmania.shared.domain.usecase

import com.msarangal.vocabmania.shared.domain.model.PracticeAction
import com.msarangal.vocabmania.shared.domain.model.ReviewRating
import kotlin.test.Test
import kotlin.test.assertEquals

class PracticeRatingMappingTest {

    @Test
    fun mapsPracticeActionsToReviewRatings() {
        assertEquals(ReviewRating.AGAIN, PracticeAction.MISSED.toReviewRating())
        assertEquals(ReviewRating.HARD, PracticeAction.ALMOST.toReviewRating())
        assertEquals(ReviewRating.GOOD, PracticeAction.GOT_IT.toReviewRating())
    }
}
