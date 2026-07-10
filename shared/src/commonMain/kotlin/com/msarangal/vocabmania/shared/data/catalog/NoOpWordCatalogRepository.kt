package com.msarangal.vocabmania.shared.data.catalog

import com.msarangal.vocabmania.shared.domain.model.WordCatalogImportState
import com.msarangal.vocabmania.shared.domain.model.WordCatalogStatus
import com.msarangal.vocabmania.shared.domain.repository.WordCatalogRepository
import com.msarangal.vocabmania.shared.domain.repository.WordRepository

class NoOpWordCatalogRepository(
    private val wordRepository: WordRepository,
) : WordCatalogRepository {
    override suspend fun importIfNeeded(nowEpochMillis: Long) = Unit

    override suspend fun getStatus(): WordCatalogStatus =
        WordCatalogStatus(
            importState = WordCatalogImportState.PENDING,
            totalWordCount = wordRepository.countWords(),
        )
}
