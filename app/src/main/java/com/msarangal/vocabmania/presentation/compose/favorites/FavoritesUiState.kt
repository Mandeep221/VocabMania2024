package com.msarangal.vocabmania.presentation.compose.favorites

data class FavoriteWordUi(
    val id: Long,
    val text: String,
    val meaning: String,
    val usageExample: String?,
)

data class FavoritesUiState(
    val isLoading: Boolean = true,
    val favorites: List<FavoriteWordUi> = emptyList(),
    val searchQuery: String = "",
    val errorMessage: String? = null,
) {
    val filteredFavorites: List<FavoriteWordUi>
        get() {
            val query = searchQuery.trim()
            if (query.isEmpty()) return favorites
            return favorites.filter { it.text.contains(query, ignoreCase = true) }
        }
}
