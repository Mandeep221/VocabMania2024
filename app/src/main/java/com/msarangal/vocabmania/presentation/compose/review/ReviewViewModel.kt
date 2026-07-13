package com.msarangal.vocabmania.presentation.compose.review

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msarangal.vocabmania.presentation.compose.navigation.Routes
import com.msarangal.vocabmania.shared.VocabManiaShared
import com.msarangal.vocabmania.shared.domain.model.ReviewRating
import com.msarangal.vocabmania.shared.domain.srs.ReviewIntervalFormatter
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ReviewViewModel(
    private val shared: VocabManiaShared,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val favoritesOnly: Boolean =
        savedStateHandle.get<Boolean>(Routes.FAVORITES_ONLY_ARG) ?: false

    private val _uiState = MutableStateFlow(ReviewUiState(favoritesOnly = favoritesOnly))
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
                    favoritesOnly = favoritesOnly,
                )
            }
            try {
                val now = System.currentTimeMillis()
                val settings = shared.getUserSettingsUseCase()
                val dueWords = shared.getDueWordsUseCase.getDueWords(
                    nowEpochMillis = now,
                    limit = settings.dailyGoal,
                    favoritesOnly = favoritesOnly,
                )
                val words = dueWords.map { dueWord ->
                    ReviewWordUi(
                        wordId = dueWord.word.id,
                        text = dueWord.word.text,
                        meaning = dueWord.word.meaning,
                        usageExample = dueWord.word.usageExample,
                        intervalDays = dueWord.reviewCard.intervalDays,
                        isFavorite = dueWord.word.isFavorite,
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

    fun toggleFavorite() {
        val state = _uiState.value
        if (state.words.isEmpty() || state.isApplyingRating) return
        val currentWord = state.words[state.currentIndex]
        viewModelScope.launch {
            try {
                val newState = shared.toggleFavoriteUseCase(currentWord.wordId) ?: return@launch
                _uiState.update { ui ->
                    val updatedWords = ui.words.toMutableList()
                    updatedWords[ui.currentIndex] = currentWord.copy(isFavorite = newState)
                    ui.copy(words = updatedWords, errorMessage = null)
                }
            } catch (error: Exception) {
                _uiState.update {
                    it.copy(errorMessage = error.message ?: "Unable to update favorite.")
                }
            }
        }
    }

    fun rate(
        rating: ReviewRating,
        onSessionComplete: (reviewedCount: Int, lastScheduleFeedback: String?) -> Unit,
    ) {
        val state = _uiState.value
        if (state.isApplyingRating || state.words.isEmpty()) return

        val currentWord = state.words[state.currentIndex]
        viewModelScope.launch {
            _uiState.update { it.copy(isApplyingRating = true, errorMessage = null, scheduleFeedback = null) }
            try {
                val schedule = shared.applyReviewRatingUseCase(
                    wordId = currentWord.wordId,
                    rating = rating,
                    nowEpochMillis = System.currentTimeMillis(),
                )
                val feedback = ReviewIntervalFormatter.formatNextReview(schedule.intervalDays)
                val reviewedCount = state.currentIndex + 1
                val nextIndex = state.currentIndex + 1
                _uiState.update {
                    it.copy(
                        isApplyingRating = false,
                        scheduleFeedback = feedback,
                    )
                }
                delay(SCHEDULE_FEEDBACK_DELAY_MS)
                if (nextIndex >= state.words.size) {
                    onSessionComplete(reviewedCount, feedback)
                } else {
                    _uiState.update {
                        it.copy(
                            currentIndex = nextIndex,
                            isMeaningRevealed = false,
                            scheduleFeedback = null,
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

    companion object {
        private const val SCHEDULE_FEEDBACK_DELAY_MS = 1_200L
    }
}
