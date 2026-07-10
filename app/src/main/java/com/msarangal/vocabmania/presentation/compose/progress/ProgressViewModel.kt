package com.msarangal.vocabmania.presentation.compose.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msarangal.vocabmania.shared.VocabManiaShared
import com.msarangal.vocabmania.shared.domain.model.DifficultyLevel
import com.msarangal.vocabmania.shared.domain.model.LevelProgress
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProgressViewModel(
    private val shared: VocabManiaShared,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProgressUiState())
    val uiState: StateFlow<ProgressUiState> = _uiState.asStateFlow()
    private var hasLoadedOnce = false

    init {
        loadProgress(isRefresh = false)
    }

    fun refresh() {
        loadProgress(isRefresh = true)
    }

    fun selectLevel(level: DifficultyLevel) {
        _uiState.update { it.copy(selectedLevel = level) }
    }

    private fun loadProgress(isRefresh: Boolean) {
        viewModelScope.launch {
            if (!isRefresh) {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            } else {
                _uiState.update { it.copy(errorMessage = null) }
            }
            try {
                val dashboard = shared.getProgressDashboardUseCase(System.currentTimeMillis())
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        selectedLevel = if (!hasLoadedOnce) {
                            dashboard.selectedLevel
                        } else {
                            it.selectedLevel
                        },
                        currentStreak = dashboard.currentStreak,
                        longestStreak = dashboard.longestStreak,
                        levelProgress = dashboard.levelProgress.mapValues { (_, progress) ->
                            progress.toUi()
                        },
                    )
                }
                hasLoadedOnce = true
            } catch (error: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Unable to load progress.",
                    )
                }
            }
        }
    }

    private fun LevelProgress.toUi(): LevelProgressUi = LevelProgressUi(
        masteryPercent = masteryPercent,
        matureCount = matureCount.toInt(),
        reviewedCount = reviewedCount.toInt(),
        activityLast7Days = activityLast7Days.toInt(),
        dailyActivity = dailyActivity.map { it.reviewCount },
    )
}
