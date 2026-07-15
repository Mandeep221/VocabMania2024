package com.msarangal.vocabmania.presentation.compose.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msarangal.vocabmania.shared.VocabManiaShared
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val shared: VocabManiaShared,
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            val showSpinner = _uiState.value.favorites.isEmpty()
            if (showSpinner) {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            } else {
                _uiState.update { it.copy(errorMessage = null) }
            }
            try {
                val now = System.currentTimeMillis()
                val favorites = shared.getFavoritesUseCase().map { word ->
                    FavoriteWordUi(
                        id = word.id,
                        text = word.text,
                        meaning = word.meaning,
                        usageExample = word.usageExample,
                    )
                }
                val favoriteDueCount = shared.getDueWordsUseCase
                    .countDue(now, favoritesOnly = true)
                    .toInt()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        favorites = favorites,
                        favoriteDueCount = favoriteDueCount,
                    )
                }
            } catch (error: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Unable to load favorites.",
                    )
                }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }
}
