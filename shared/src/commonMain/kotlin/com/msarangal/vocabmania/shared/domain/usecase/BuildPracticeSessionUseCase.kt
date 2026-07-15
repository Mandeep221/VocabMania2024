package com.msarangal.vocabmania.shared.domain.usecase

import com.msarangal.vocabmania.shared.domain.model.PracticeCard
import com.msarangal.vocabmania.shared.domain.repository.ReviewRepository
import com.msarangal.vocabmania.shared.domain.repository.UserSettingsRepository

/**
 * Builds today's Practice bite: N = min(due, goal), new-first with new-cap, then repeats.
 */
class BuildPracticeSessionUseCase(
    private val reviewRepository: ReviewRepository,
    private val userSettingsRepository: UserSettingsRepository,
) {
    suspend operator fun invoke(
        nowEpochMillis: Long,
        favoritesOnly: Boolean = false,
    ): List<PracticeCard> {
        val settings = userSettingsRepository.getSettings()
        val dueCount = reviewRepository.countDueWords(
            level = settings.selectedLevel,
            nowEpochMillis = nowEpochMillis,
            favoritesOnly = favoritesOnly,
        ).toInt()
        val n = practiceSessionBiteSize(dueCount, settings.dailyGoal)
        if (n == 0) return emptyList()

        val candidates = reviewRepository.getDueWords(
            level = settings.selectedLevel,
            nowEpochMillis = nowEpochMillis,
            limit = dueCount.coerceAtLeast(1),
            favoritesOnly = favoritesOnly,
        )
        return orderPracticeSession(candidates, n)
    }
}
