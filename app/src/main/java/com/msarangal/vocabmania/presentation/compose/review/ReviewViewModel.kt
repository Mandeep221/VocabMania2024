package com.msarangal.vocabmania.presentation.compose.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msarangal.vocabmania.shared.VocabManiaShared
import com.msarangal.vocabmania.shared.domain.model.ReviewRating
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ReviewViewModel(
    private val shared: VocabManiaShared,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReviewUiState())
    val uiState: StateFlow<ReviewUiState> = _uiState.asStateFlow()

    init {
        loadSession()
    }

    fun loadSession() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                    isEmpty = false,
                    currentIndex = 0,
                    isMeaningRevealed = false,
                )
            }
            try {
                val now = System.currentTimeMillis()
                val settings = shared.getUserSettingsUseCase()
                val dueWords = shared.getDueWordsUseCase.getDueWords(now, limit = settings.dailyGoal)
                val words = dueWords.map { dueWord ->
                    ReviewWordUi(
                        wordId = dueWord.word.id,
                        text = dueWord.word.text,
                        meaning = dueWord.word.meaning,
                        usageExample = dueWord.word.usageExample,
                    )
                }
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        words = words,
                        isEmpty = words.isEmpty(),
                    )
                }
            } catch (error: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Unable to load review session.",
                    )
                }
            }
        }
    }

    fun revealMeaning() {
        _uiState.update { it.copy(isMeaningRevealed = true) }
    }

    fun rate(rating: ReviewRating, onSessionComplete: (reviewedCount: Int) -> Unit) {
        val state = _uiState.value
        if (state.isApplyingRating || state.words.isEmpty()) return

        val currentWord = state.words[state.currentIndex]
        viewModelScope.launch {
            _uiState.update { it.copy(isApplyingRating = true, errorMessage = null) }
            try {
                shared.applyReviewRatingUseCase(
                    wordId = currentWord.wordId,
                    rating = rating,
                    nowEpochMillis = System.currentTimeMillis(),
                )
                val reviewedCount = state.currentIndex + 1
                val nextIndex = state.currentIndex + 1
                if (nextIndex >= state.words.size) {
                    onSessionComplete(reviewedCount)
                } else {
                    _uiState.update {
                        it.copy(
                            currentIndex = nextIndex,
                            isMeaningRevealed = false,
                            isApplyingRating = false,
                        )
                    }
                }
            } catch (error: Exception) {
                _uiState.update {
                    it.copy(
                        isApplyingRating = false,
                        errorMessage = error.message ?: "Unable to save rating.",
                    )
                }
            }
        }
    }
}
