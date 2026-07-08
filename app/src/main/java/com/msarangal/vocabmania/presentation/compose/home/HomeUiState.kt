package com.msarangal.vocabmania.presentation.compose.home

data class HomeUiState(
    val isLoading: Boolean = true,
    val currentStreak: Int = 0,
    val dueCount: Int = 0,
    val dailyGoal: Int = 15,
    val selectedLevelLabel: String = "Easy",
    val errorMessage: String? = null,
)
