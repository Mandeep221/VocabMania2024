package com.msarangal.vocabmania.shared.domain.usecase

import com.msarangal.vocabmania.shared.domain.model.Word
import com.msarangal.vocabmania.shared.domain.repository.WordRepository

class GetFavoritesUseCase(
    private val wordRepository: WordRepository,
) {
    suspend operator fun invoke(): List<Word> = wordRepository.getFavorites()
}

class ToggleFavoriteUseCase(
    private val wordRepository: WordRepository,
) {
    /**
     * Flips favorite state for [wordId].
     * @return new favorite state, or null if the word does not exist.
     */
    suspend operator fun invoke(wordId: Long): Boolean? {
        val word = wordRepository.getWord(wordId) ?: return null
        val newState = !word.isFavorite
        wordRepository.setFavorite(wordId, newState)
        return newState
    }
}
