package com.msarangal.vocabmania.presentation.compose.navigation

object Routes {
    const val ONBOARDING = "onboarding"
    const val HOME = "home"
    const val REVIEW = "review"
    const val SESSION_COMPLETE = "session_complete/{reviewedCount}"

    const val REVIEWED_COUNT_ARG = "reviewedCount"

    fun sessionComplete(reviewedCount: Int): String = "session_complete/$reviewedCount"
}
