package com.msarangal.vocabmania.shared.domain.model

enum class WordCatalogImportState {
    PENDING,
    IMPORTING,
    COMPLETE,
    FAILED,
}

data class WordCatalogStatus(
    val importState: WordCatalogImportState,
    val totalWordCount: Long,
)

object MigrationKeys {
    const val FIREBASE_WORD_CATALOG_IMPORT = "firebase_word_catalog_import"
    const val FIREBASE_WORD_CATALOG_IMPORT_FAILED = "firebase_word_catalog_import_failed"
}
