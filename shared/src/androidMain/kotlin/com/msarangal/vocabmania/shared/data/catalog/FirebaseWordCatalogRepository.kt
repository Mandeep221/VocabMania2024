package com.msarangal.vocabmania.shared.data.catalog

import com.msarangal.vocabmania.shared.data.firebase.FirebaseWordCatalogImporter
import com.msarangal.vocabmania.shared.data.seed.SeedDataLoader
import com.msarangal.vocabmania.shared.domain.model.MigrationKeys
import com.msarangal.vocabmania.shared.domain.model.WordCatalogImportState
import com.msarangal.vocabmania.shared.domain.model.WordCatalogStatus
import com.msarangal.vocabmania.shared.domain.repository.MigrationRepository
import com.msarangal.vocabmania.shared.domain.repository.WordCatalogRepository
import com.msarangal.vocabmania.shared.domain.repository.WordRepository
import java.util.concurrent.atomic.AtomicBoolean

class FirebaseWordCatalogRepository(
    private val importer: FirebaseWordCatalogImporter,
    private val wordRepository: WordRepository,
    private val migrationRepository: MigrationRepository,
    private val seedDataLoader: SeedDataLoader,
) : WordCatalogRepository {
    private val isImporting = AtomicBoolean(false)

    override suspend fun importIfNeeded(nowEpochMillis: Long) {
        if (migrationRepository.isMigrationComplete(MigrationKeys.FIREBASE_WORD_CATALOG_IMPORT)) {
            return
        }
        if (!isImporting.compareAndSet(false, true)) {
            return
        }

        try {
            importer.importAll(nowEpochMillis)
            migrationRepository.markMigrationComplete(
                MigrationKeys.FIREBASE_WORD_CATALOG_IMPORT,
                nowEpochMillis,
            )
            if (wordRepository.countWords() == 0L) {
                seedDataLoader.seedIfEmpty(nowEpochMillis)
            }
        } catch (_: Exception) {
            migrationRepository.markMigrationComplete(
                MigrationKeys.FIREBASE_WORD_CATALOG_IMPORT_FAILED,
                nowEpochMillis,
            )
            seedDataLoader.seedIfEmpty(nowEpochMillis)
        } finally {
            isImporting.set(false)
        }
    }

    override suspend fun getStatus(): WordCatalogStatus {
        val totalWordCount = wordRepository.countWords()
        val importState = when {
            migrationRepository.isMigrationComplete(MigrationKeys.FIREBASE_WORD_CATALOG_IMPORT) ->
                WordCatalogImportState.COMPLETE
            isImporting.get() ->
                WordCatalogImportState.IMPORTING
            migrationRepository.isMigrationComplete(MigrationKeys.FIREBASE_WORD_CATALOG_IMPORT_FAILED) ->
                WordCatalogImportState.FAILED
            else ->
                WordCatalogImportState.PENDING
        }

        return WordCatalogStatus(
            importState = importState,
            totalWordCount = totalWordCount,
        )
    }
}
