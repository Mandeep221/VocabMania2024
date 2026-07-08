package com.msarangal.vocabmania.shared.domain.usecase

import com.msarangal.vocabmania.shared.domain.model.DifficultyLevel
import com.msarangal.vocabmania.shared.domain.model.DueWord
import com.msarangal.vocabmania.shared.domain.model.ReviewRating
import com.msarangal.vocabmania.shared.domain.model.SessionSummary
import com.msarangal.vocabmania.shared.domain.model.UserSettings
import com.msarangal.vocabmania.shared.domain.repository.ReviewRepository
import com.msarangal.vocabmania.shared.domain.repository.UserSettingsRepository

class GetDueWordsUseCase(
    private val reviewRepository: ReviewRepository,
    private val userSettingsRepository: UserSettingsRepository,
) {
    suspend fun countDue(nowEpochMillis: Long): Long {
        val level = userSettingsRepository.getSettings().selectedLevel
        return reviewRepository.countDueWords(level, nowEpochMillis)
    }

    suspend fun getDueWords(nowEpochMillis: Long, limit: Int? = null): List<DueWord> {
        val settings = userSettingsRepository.getSettings()
        val effectiveLimit = limit ?: settings.dailyGoal
        return reviewRepository.getDueWords(settings.selectedLevel, nowEpochMillis, effectiveLimit)
    }
}

class ApplyReviewRatingUseCase(
    private val reviewRepository: ReviewRepository,
) {
    suspend operator fun invoke(
        wordId: Long,
        rating: ReviewRating,
        nowEpochMillis: Long,
    ) {
        reviewRepository.applyRating(wordId, rating, nowEpochMillis)
    }
}

class CompleteReviewSessionUseCase(
    private val userSettingsRepository: UserSettingsRepository,
) {
    suspend operator fun invoke(
        nowEpochMillis: Long,
        reviewedCount: Int,
    ): SessionSummary = userSettingsRepository.completeSession(nowEpochMillis, reviewedCount)
}

class GetUserSettingsUseCase(
    private val userSettingsRepository: UserSettingsRepository,
) {
    suspend operator fun invoke(): UserSettings = userSettingsRepository.getSettings()
}

class SaveUserSettingsUseCase(
    private val userSettingsRepository: UserSettingsRepository,
) {
    suspend operator fun invoke(settings: UserSettings) {
        userSettingsRepository.saveSettings(settings)
    }
}

class CompleteOnboardingUseCase(
    private val userSettingsRepository: UserSettingsRepository,
) {
    suspend operator fun invoke(
        level: DifficultyLevel,
        dailyGoal: Int,
    ) {
        val current = userSettingsRepository.getSettings()
        userSettingsRepository.saveSettings(
            current.copy(
                onboardingComplete = true,
                selectedLevel = level,
                dailyGoal = dailyGoal,
            ),
        )
    }
}
