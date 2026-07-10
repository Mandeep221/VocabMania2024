package com.msarangal.vocabmania.shared.domain.repository

import com.msarangal.vocabmania.shared.domain.model.WordCatalogStatus

interface WordCatalogRepository {
    suspend fun importIfNeeded(nowEpochMillis: Long)
    suspend fun getStatus(): WordCatalogStatus
}
