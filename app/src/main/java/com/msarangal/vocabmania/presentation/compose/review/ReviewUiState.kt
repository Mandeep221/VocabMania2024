package com.msarangal.vocabmania.presentation.compose.review

data class ReviewWordUi(
    val wordId: Long,
    val text: String,
    val meaning: String,
    val usageExample: String?,
)

data class ReviewUiState(
    val isLoading: Boolean = true,
    val words: List<ReviewWordUi> = emptyList(),
    val currentIndex: Int = 0,
    val isMeaningRevealed: Boolean = false,
    val isApplyingRating: Boolean = false,
    val isEmpty: Boolean = false,
    val errorMessage: String? = null,
)
