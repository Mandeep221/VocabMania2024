package com.msarangal.vocabmania.shared.domain.usecase

import com.msarangal.vocabmania.shared.domain.model.WordCatalogStatus
import com.msarangal.vocabmania.shared.domain.repository.WordCatalogRepository

class ImportWordCatalogUseCase(
    private val wordCatalogRepository: WordCatalogRepository,
) {
    suspend operator fun invoke(nowEpochMillis: Long) {
        wordCatalogRepository.importIfNeeded(nowEpochMillis)
    }
}

class GetWordCatalogStatusUseCase(
    private val wordCatalogRepository: WordCatalogRepository,
) {
    suspend operator fun invoke(): WordCatalogStatus = wordCatalogRepository.getStatus()
}
