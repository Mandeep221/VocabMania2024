package com.msarangal.vocabmania.shared.data.catalog

import com.msarangal.vocabmania.shared.domain.model.DifficultyLevel

data class MappedFirebaseWord(
    val text: String,
    val meaning: String,
    val usageExample: String?,
    val level: DifficultyLevel,
    val firebaseLevelAttempt: String,
)

object FirebaseWordRecordMapper {
    fun map(
        word: String?,
        question: String?,
        op1: String?,
        op2: String?,
        op3: String?,
        answerRaw: String?,
        levelAttempt: String?,
    ): MappedFirebaseWord? {
        val text = word?.trim()?.takeIf { it.isNotEmpty() } ?: return null
        val attempt = levelAttempt?.trim()?.takeIf { it.isNotEmpty() } ?: return null
        val levelPrefix = attempt.substringBefore("_").trim()
        if (levelPrefix.isEmpty()) return null

        val answer = answerRaw?.trim()?.toIntOrNull() ?: return null
        if (answer !in 1..3) return null

        val options = listOf(op1, op2, op3)
        val meaning = options.getOrNull(answer - 1)?.trim()?.takeIf { it.isNotEmpty() } ?: return null

        return MappedFirebaseWord(
            text = text,
            meaning = meaning,
            usageExample = question?.trim()?.takeIf { it.isNotEmpty() },
            level = DifficultyLevel.fromCode(levelPrefix),
            firebaseLevelAttempt = attempt,
        )
    }
}
