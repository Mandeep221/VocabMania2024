package com.msarangal.vocabmania.presentation.compose.onboarding

import com.msarangal.vocabmania.shared.domain.model.DifficultyLevel

enum class OnboardingStep {
    WELCOME,
    LEVEL,
    DAILY_GOAL,
}

data class OnboardingUiState(
    val step: OnboardingStep = OnboardingStep.WELCOME,
    val selectedLevel: DifficultyLevel = DifficultyLevel.EASY,
    val selectedDailyGoal: Int = 15,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
)
