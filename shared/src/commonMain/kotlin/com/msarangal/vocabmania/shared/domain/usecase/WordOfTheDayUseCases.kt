package com.msarangal.vocabmania.shared.domain.usecase

import com.msarangal.vocabmania.shared.domain.model.WordOfTheDay
import com.msarangal.vocabmania.shared.domain.repository.WordOfTheDayRepository

class GetWordOfTheDayUseCase(
    private val wordOfTheDayRepository: WordOfTheDayRepository,
) {
    suspend operator fun invoke(nowEpochMillis: Long): WordOfTheDay? =
        wordOfTheDayRepository.getWordOfTheDay(nowEpochMillis)
}
