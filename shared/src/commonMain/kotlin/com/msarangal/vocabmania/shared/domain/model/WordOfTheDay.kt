package com.msarangal.vocabmania.shared.domain.model

data class WordOfTheDay(
    val word: String,
    val meaning: String,
    val usageExample: String?,
    val fetchedAtEpochMillis: Long,
    val isFromCache: Boolean,
)
