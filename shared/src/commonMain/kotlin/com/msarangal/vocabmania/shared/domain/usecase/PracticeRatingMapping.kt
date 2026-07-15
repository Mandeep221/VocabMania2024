package com.msarangal.vocabmania.shared.domain.usecase

import com.msarangal.vocabmania.shared.domain.model.PracticeAction
import com.msarangal.vocabmania.shared.domain.model.ReviewRating

fun PracticeAction.toReviewRating(): ReviewRating = when (this) {
    PracticeAction.MISSED -> ReviewRating.AGAIN
    PracticeAction.ALMOST -> ReviewRating.HARD
    PracticeAction.GOT_IT -> ReviewRating.GOOD
}
