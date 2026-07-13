package com.msarangal.vocabmania.presentation.compose.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msarangal.vocabmania.shared.VocabManiaShared
import com.msarangal.vocabmania.shared.domain.model.DifficultyLevel
import com.msarangal.vocabmania.shared.domain.model.WordCatalogImportState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val shared: VocabManiaShared,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val now = System.currentTimeMillis()
                val settings = shared.getUserSettingsUseCase()
                val dueCount = shared.getDueWordsUseCase.countDue(now).toInt()
                val favoriteDueCount = shared.getDueWordsUseCase.countDue(now, favoritesOnly = true).toInt()
                val catalogStatus = shared.getWordCatalogStatusUseCase()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        currentStreak = settings.currentStreak,
                        dueCount = dueCount,
                        favoriteDueCount = favoriteDueCount,
                        dailyGoal = settings.dailyGoal,
                        selectedLevelLabel = settings.selectedLevel.toDisplayLabel(),
                        totalWordCount = catalogStatus.totalWordCount.toInt(),
                        catalogImportState = catalogStatus.importState,
                    )
                }

                loadWordOfTheDay(now)

                if (catalogStatus.importState == WordCatalogImportState.IMPORTING ||
                    catalogStatus.importState == WordCatalogImportState.PENDING
                ) {
                    delay(1_500)
                    refresh()
                }
            } catch (error: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Unable to load home data.",
                    )
                }
            }
        }
    }

    private suspend fun loadWordOfTheDay(nowEpochMillis: Long) {
        val showLoading = _uiState.value.wordOfTheDay == null
        if (showLoading) {
            _uiState.update { it.copy(isWordOfTheDayLoading = true) }
        }
        try {
            val wotd = shared.getWordOfTheDayUseCase(nowEpochMillis)
            _uiState.update {
                it.copy(
                    isWordOfTheDayLoading = false,
                    wordOfTheDay = wotd?.let { word ->
                        WordOfTheDayUi(
                            word = word.word,
                            meaning = word.meaning,
                            usageExample = word.usageExample,
                            isFromCache = word.isFromCache,
                        )
                    },
                )
            }
        } catch (_: Exception) {
            _uiState.update { it.copy(isWordOfTheDayLoading = false) }
        }
    }

    private fun DifficultyLevel.toDisplayLabel(): String = when (this) {
        DifficultyLevel.EASY -> "Easy"
        DifficultyLevel.MEDIUM -> "Medium"
        DifficultyLevel.TOUGH -> "Tough"
    }
}
