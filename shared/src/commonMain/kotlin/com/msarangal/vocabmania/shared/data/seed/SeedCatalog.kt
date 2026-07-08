package com.msarangal.vocabmania.shared.data.seed

import com.msarangal.vocabmania.shared.domain.model.DifficultyLevel
import com.msarangal.vocabmania.shared.domain.repository.ReviewRepository
import com.msarangal.vocabmania.shared.domain.repository.WordRepository

data class SeedWord(
    val text: String,
    val meaning: String,
    val usageExample: String?,
    val level: DifficultyLevel,
)

object SeedCatalog {
    val words: List<SeedWord> = listOf(
        SeedWord("ephemeral", "lasting for a very short time", "The ephemeral beauty of cherry blossoms draws crowds each spring.", DifficultyLevel.EASY),
        SeedWord("benevolent", "well meaning and kindly", "The benevolent donor funded the new library wing.", DifficultyLevel.EASY),
        SeedWord("candid", "truthful and straightforward", "She gave a candid assessment of the prototype.", DifficultyLevel.EASY),
        SeedWord("diligent", "showing care in one's work", "His diligent preparation showed in the final presentation.", DifficultyLevel.EASY),
        SeedWord("eloquent", "fluent and persuasive in speaking", "The speaker delivered an eloquent closing argument.", DifficultyLevel.EASY),
        SeedWord("pragmatic", "dealing with things sensibly and realistically", "We took a pragmatic approach to the rollout plan.", DifficultyLevel.MEDIUM),
        SeedWord("ambiguous", "open to more than one interpretation", "The contract language was deliberately ambiguous.", DifficultyLevel.MEDIUM),
        SeedWord("meticulous", "showing great attention to detail", "Meticulous testing prevented a production regression.", DifficultyLevel.MEDIUM),
        SeedWord("resilient", "able to recover quickly from difficulty", "The team remained resilient after the failed launch.", DifficultyLevel.MEDIUM),
        SeedWord("scrutinize", "to examine closely and thoroughly", "Auditors scrutinize expense reports each quarter.", DifficultyLevel.MEDIUM),
        SeedWord("obfuscate", "to make unclear or unintelligible", "Jargon can obfuscate simple product decisions.", DifficultyLevel.TOUGH),
        SeedWord("recalcitrant", "having an obstinately uncooperative attitude", "The recalcitrant module resisted every refactor attempt.", DifficultyLevel.TOUGH),
        SeedWord("surreptitious", "kept secret, especially because it would not be approved of", "He took a surreptitious glance at the roadmap slide.", DifficultyLevel.TOUGH),
        SeedWord("perfunctory", "carried out with minimum effort or reflection", "A perfunctory code review missed the regression.", DifficultyLevel.TOUGH),
        SeedWord("vicarious", "experienced through another person", "Parents often feel vicarious pride at graduation.", DifficultyLevel.TOUGH),
    )
}

class SeedDataLoader(
    private val wordRepository: WordRepository,
    private val reviewRepository: ReviewRepository,
) {
    suspend fun seedIfEmpty(nowEpochMillis: Long) {
        if (wordRepository.countWords() > 0) return

        SeedCatalog.words.forEach { seed ->
            val wordId = wordRepository.insertWord(
                text = seed.text,
                meaning = seed.meaning,
                usageExample = seed.usageExample,
                level = seed.level,
            )
            reviewRepository.ensureReviewCard(wordId, nowEpochMillis)
        }
    }
}
