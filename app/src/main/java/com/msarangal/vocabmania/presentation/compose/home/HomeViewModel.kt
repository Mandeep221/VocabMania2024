package com.msarangal.vocabmania.presentation.compose.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msarangal.vocabmania.shared.VocabManiaShared
import com.msarangal.vocabmania.shared.domain.model.DifficultyLevel
import com.msarangal.vocabmania.shared.domain.model.WordCatalogImportState
import com.msarangal.vocabmania.shared.domain.reminder.DailyReminderScheduler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val shared: VocabManiaShared,
    private val reminderScheduler: DailyReminderScheduler,
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

                if (settings.dailyReminderEnabled) {
                    reminderScheduler.scheduleDaily()
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        currentStreak = settings.currentStreak,
                        dueCount = dueCount,
                        favoriteDueCount = favoriteDueCount,
                        dailyGoal = settings.dailyGoal,
                        selectedLevelLabel = settings.selectedLevel.toDisplayLabel(),
                        catalogImportState = catalogStatus.importState,
                        dailyReminderEnabled = settings.dailyReminderEnabled,
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

    /** Optimistic UI while the system permission dialog is open. */
    fun markReminderPendingPermission() {
        _uiState.update { it.copy(dailyReminderEnabled = true, errorMessage = null) }
    }

    /**
     * Called after permission grant (when enabling) or immediately when disabling.
     * Permission denial should call [revertReminderOff] instead of this with enabled=true.
     */
    fun setDailyReminderEnabled(enabled: Boolean) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    dailyReminderEnabled = enabled,
                    isReminderUpdating = true,
                    errorMessage = null,
                )
            }
            try {
                shared.setDailyReminderEnabledUseCase(enabled)
                if (enabled) {
                    reminderScheduler.scheduleDaily()
                } else {
                    reminderScheduler.cancel()
                }
                _uiState.update { it.copy(isReminderUpdating = false) }
            } catch (error: Exception) {
                _uiState.update {
                    it.copy(
                        dailyReminderEnabled = !enabled,
                        isReminderUpdating = false,
                        errorMessage = error.message ?: "Unable to update reminder.",
                    )
                }
            }
        }
    }

    fun revertReminderOff() {
        _uiState.update {
            it.copy(
                dailyReminderEnabled = false,
                isReminderUpdating = false,
            )
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
