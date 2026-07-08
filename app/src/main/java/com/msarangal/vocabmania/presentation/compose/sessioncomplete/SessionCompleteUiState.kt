package com.msarangal.vocabmania.presentation.compose.sessioncomplete

data class SessionCompleteUiState(
    val isLoading: Boolean = true,
    val reviewedCount: Int = 0,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val errorMessage: String? = null,
)
