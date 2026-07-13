package com.msarangal.vocabmania.presentation.compose.navigation

object Routes {
    const val ONBOARDING = "onboarding"
    const val HOME = "home"
    const val REVIEW = "review?favoritesOnly={favoritesOnly}"
    const val PROGRESS = "progress"
    const val FAVORITES = "favorites"
    const val SESSION_COMPLETE = "session_complete/{reviewedCount}?lastScheduleFeedback={lastScheduleFeedback}"

    const val FAVORITES_ONLY_ARG = "favoritesOnly"
    const val REVIEWED_COUNT_ARG = "reviewedCount"
    const val LAST_SCHEDULE_FEEDBACK_ARG = "lastScheduleFeedback"

    fun review(favoritesOnly: Boolean = false): String =
        "review?favoritesOnly=$favoritesOnly"

    fun sessionComplete(
        reviewedCount: Int,
        lastScheduleFeedback: String? = null,
    ): String {
        val feedback = lastScheduleFeedback?.let { android.net.Uri.encode(it) }.orEmpty()
        return "session_complete/$reviewedCount?lastScheduleFeedback=$feedback"
    }
}
