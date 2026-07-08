package com.msarangal.vocabmania.presentation.compose.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msarangal.vocabmania.shared.VocabManiaShared
import com.msarangal.vocabmania.shared.domain.model.DifficultyLevel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val shared: VocabManiaShared,
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun goToNextStep() {
        _uiState.update { state ->
            val nextStep = when (state.step) {
                OnboardingStep.WELCOME -> OnboardingStep.LEVEL
                OnboardingStep.LEVEL -> OnboardingStep.DAILY_GOAL
                OnboardingStep.DAILY_GOAL -> OnboardingStep.DAILY_GOAL
            }
            state.copy(step = nextStep, errorMessage = null)
        }
    }

    fun selectLevel(level: DifficultyLevel) {
        _uiState.update { it.copy(selectedLevel = level) }
    }

    fun selectDailyGoal(goal: Int) {
        _uiState.update { it.copy(selectedDailyGoal = goal) }
    }

    fun completeOnboarding(onComplete: () -> Unit) {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            try {
                shared.completeOnboardingUseCase(
                    level = state.selectedLevel,
                    dailyGoal = state.selectedDailyGoal,
                )
                onComplete()
            } catch (error: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = error.message ?: "Unable to save onboarding settings.",
                    )
                }
            }
        }
    }
}
