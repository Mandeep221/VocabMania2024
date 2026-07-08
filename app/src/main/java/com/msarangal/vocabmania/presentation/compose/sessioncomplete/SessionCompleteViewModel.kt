package com.msarangal.vocabmania.presentation.compose.sessioncomplete

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msarangal.vocabmania.presentation.compose.navigation.Routes
import com.msarangal.vocabmania.shared.VocabManiaShared
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SessionCompleteViewModel(
    private val shared: VocabManiaShared,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val reviewedCount: Int = savedStateHandle.get<Int>(Routes.REVIEWED_COUNT_ARG) ?: 0

    private val _uiState = MutableStateFlow(SessionCompleteUiState(reviewedCount = reviewedCount))
    val uiState: StateFlow<SessionCompleteUiState> = _uiState.asStateFlow()

    init {
        completeSession()
    }

    private fun completeSession() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val summary = shared.completeReviewSessionUseCase(
                    nowEpochMillis = System.currentTimeMillis(),
                    reviewedCount = reviewedCount,
                )
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        reviewedCount = summary.reviewedCount,
                        currentStreak = summary.currentStreak,
                        longestStreak = summary.longestStreak,
                    )
                }
            } catch (error: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Unable to complete session.",
                    )
                }
            }
        }
    }
}
