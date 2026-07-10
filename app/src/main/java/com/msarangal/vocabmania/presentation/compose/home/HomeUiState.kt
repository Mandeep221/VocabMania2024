package com.msarangal.vocabmania.presentation.compose.home

import com.msarangal.vocabmania.shared.domain.model.WordCatalogImportState

data class HomeUiState(
    val isLoading: Boolean = true,
    val currentStreak: Int = 0,
    val dueCount: Int = 0,
    val dailyGoal: Int = 15,
    val selectedLevelLabel: String = "Easy",
    val totalWordCount: Int = 0,
    val catalogImportState: WordCatalogImportState = WordCatalogImportState.PENDING,
    val errorMessage: String? = null,
)
