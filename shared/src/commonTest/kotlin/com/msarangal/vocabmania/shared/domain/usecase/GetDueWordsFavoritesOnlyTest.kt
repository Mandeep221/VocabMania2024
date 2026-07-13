package com.msarangal.vocabmania.shared.domain.usecase

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.msarangal.vocabmania.shared.data.repository.SqlDelightReviewRepository
import com.msarangal.vocabmania.shared.data.repository.SqlDelightUserSettingsRepository
import com.msarangal.vocabmania.shared.data.repository.SqlDelightWordRepository
import com.msarangal.vocabmania.shared.db.VocabManiaDatabase
import com.msarangal.vocabmania.shared.domain.model.DifficultyLevel
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class GetDueWordsFavoritesOnlyTest {

    @Test
    fun favoritesOnlyReturnsOnlyFavoritedDueWords() = runBlocking {
        val (useCase, now) = createUseCaseWithDueWords()

        val allDue = useCase.getDueWords(now, limit = 10, favoritesOnly = false)
        val favoritesDue = useCase.getDueWords(now, limit = 10, favoritesOnly = true)

        assertEquals(2, allDue.size)
        assertEquals(1, favoritesDue.size)
        assertEquals("favorite-due", favoritesDue.single().word.text)
        assertEquals(1, useCase.countDue(now, favoritesOnly = true))
        assertEquals(2, useCase.countDue(now, favoritesOnly = false))
    }

    @Test
    fun favoritesOnlyEmptyWhenNoFavoritedDueWords() = runBlocking {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        VocabManiaDatabase.Schema.create(driver)
        val database = VocabManiaDatabase(driver)
        val wordRepository = SqlDelightWordRepository(database)
        val reviewRepository = SqlDelightReviewRepository(database)
        val settingsRepository = SqlDelightUserSettingsRepository(database)
        settingsRepository.saveSettings(
            settingsRepository.getSettings().copy(
                onboardingComplete = true,
                selectedLevel = DifficultyLevel.EASY,
                dailyGoal = 10,
            ),
        )
        val now = 1_700_000_000_000L
        val plainId = wordRepository.insertWord(
            text = "plain",
            meaning = "meaning",
            usageExample = null,
            level = DifficultyLevel.EASY,
        )
        reviewRepository.ensureReviewCard(plainId, now)

        val useCase = GetDueWordsUseCase(reviewRepository, settingsRepository)
        assertEquals(0, useCase.getDueWords(now, favoritesOnly = true).size)
        assertEquals(0, useCase.countDue(now, favoritesOnly = true))
    }

    private suspend fun createUseCaseWithDueWords(): Pair<GetDueWordsUseCase, Long> {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        VocabManiaDatabase.Schema.create(driver)
        val database = VocabManiaDatabase(driver)
        val wordRepository = SqlDelightWordRepository(database)
        val reviewRepository = SqlDelightReviewRepository(database)
        val settingsRepository = SqlDelightUserSettingsRepository(database)
        settingsRepository.saveSettings(
            settingsRepository.getSettings().copy(
                onboardingComplete = true,
                selectedLevel = DifficultyLevel.EASY,
                dailyGoal = 10,
            ),
        )
        val now = 1_700_000_000_000L

        val favoriteId = wordRepository.insertWord(
            text = "favorite-due",
            meaning = "meaning",
            usageExample = null,
            level = DifficultyLevel.EASY,
            isFavorite = true,
        )
        val plainId = wordRepository.insertWord(
            text = "plain-due",
            meaning = "meaning",
            usageExample = null,
            level = DifficultyLevel.EASY,
        )
        reviewRepository.ensureReviewCard(favoriteId, now)
        reviewRepository.ensureReviewCard(plainId, now)

        return GetDueWordsUseCase(reviewRepository, settingsRepository) to now
    }
}
