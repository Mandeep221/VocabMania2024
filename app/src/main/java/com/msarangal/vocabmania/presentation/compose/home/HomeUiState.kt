package com.msarangal.vocabmania.presentation.compose.home

import com.msarangal.vocabmania.shared.domain.model.WordCatalogImportState
import com.msarangal.vocabmania.shared.domain.usecase.practiceSessionBiteSize
import com.msarangal.vocabmania.shared.domain.usecase.practiceShowsQueueLine

data class WordOfTheDayUi(
    val word: String,
    val meaning: String,
    val usageExample: String?,
    val isFromCache: Boolean,
)

data class HomeUiState(
    val isLoading: Boolean = true,
    val currentStreak: Int = 0,
    val dueCount: Int = 0,
    val favoriteDueCount: Int = 0,
    val dailyGoal: Int = 15,
    val selectedLevelLabel: String = "Easy",
    val catalogImportState: WordCatalogImportState = WordCatalogImportState.PENDING,
    val wordOfTheDay: WordOfTheDayUi? = null,
    val isWordOfTheDayLoading: Boolean = false,
    val dailyReminderEnabled: Boolean = false,
    val isReminderUpdating: Boolean = false,
    val errorMessage: String? = null,
) {
    val sessionBiteSize: Int
        get() = practiceSessionBiteSize(dueCount, dailyGoal)

    val showQueueLine: Boolean
        get() = practiceShowsQueueLine(dueCount, dailyGoal)
}
