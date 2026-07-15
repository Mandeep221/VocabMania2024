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
import kotlin.test.assertTrue

class BuildPracticeSessionUseCaseTest {

    @Test
    fun buildsMixedSessionNewFirstWithinCapAndRespectsN() = runBlocking {
        val fixture = createFixture(dailyGoal = 4)
        val now = fixture.now

        // 3 new + 3 repeats due; N=4 → newCap=2 → 2 new then 2 repeats
        // Stagger next_review_at slightly earlier than now so all are due and order is stable.
        fixture.insertDueWord("new-a", reviewCount = 0, nextAt = now - 6)
        fixture.insertDueWord("new-b", reviewCount = 0, nextAt = now - 5)
        fixture.insertDueWord("new-c", reviewCount = 0, nextAt = now - 4)
        fixture.insertDueWord("rep-a", reviewCount = 2, nextAt = now - 3)
        fixture.insertDueWord("rep-b", reviewCount = 1, nextAt = now - 2)
        fixture.insertDueWord("rep-c", reviewCount = 3, nextAt = now - 1)

        val session = fixture.useCase(nowEpochMillis = now)
        assertEquals(4, session.size)
        assertEquals(listOf("new-a", "new-b", "rep-a", "rep-b"), session.map { it.word.text })
        assertTrue(session[0].isNew && session[1].isNew)
        assertTrue(!session[2].isNew && !session[3].isNew)
    }

    @Test
    fun favoritesOnlyUsesSameBuilderRules() = runBlocking {
        val fixture = createFixture(dailyGoal = 10)
        val now = fixture.now

        fixture.insertDueWord("fav-new", reviewCount = 0, nextAt = now - 3, favorite = true)
        fixture.insertDueWord("fav-rep", reviewCount = 2, nextAt = now - 2, favorite = true)
        fixture.insertDueWord("plain-new", reviewCount = 0, nextAt = now - 1, favorite = false)

        val session = fixture.useCase(nowEpochMillis = now, favoritesOnly = true)
        assertEquals(listOf("fav-new", "fav-rep"), session.map { it.word.text })
        assertTrue(session[0].isNew)
        assertTrue(!session[1].isNew)
    }

    @Test
    fun emptyWhenNothingDue() = runBlocking {
        val fixture = createFixture(dailyGoal = 10)
        assertEquals(0, fixture.useCase(nowEpochMillis = fixture.now).size)
    }

    private fun createFixture(dailyGoal: Int): Fixture {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        VocabManiaDatabase.Schema.create(driver)
        val database = VocabManiaDatabase(driver)
        val wordRepository = SqlDelightWordRepository(database)
        val reviewRepository = SqlDelightReviewRepository(database)
        val settingsRepository = SqlDelightUserSettingsRepository(database)
        runBlocking {
            settingsRepository.saveSettings(
                settingsRepository.getSettings().copy(
                    onboardingComplete = true,
                    selectedLevel = DifficultyLevel.EASY,
                    dailyGoal = dailyGoal,
                ),
            )
        }
        return Fixture(
            database = database,
            wordRepository = wordRepository,
            useCase = BuildPracticeSessionUseCase(reviewRepository, settingsRepository),
            now = 1_700_000_000_000L,
        )
    }

    private class Fixture(
        val database: VocabManiaDatabase,
        val wordRepository: SqlDelightWordRepository,
        val useCase: BuildPracticeSessionUseCase,
        val now: Long,
    ) {
        suspend fun insertDueWord(
            text: String,
            reviewCount: Int,
            nextAt: Long,
            favorite: Boolean = false,
        ) {
            val id = wordRepository.insertWord(
                text = text,
                meaning = "meaning",
                usageExample = null,
                level = DifficultyLevel.EASY,
                isFavorite = favorite,
            )
            database.reviewCardQueries.insertReviewCard(
                word_id = id,
                next_review_at = nextAt,
                interval_days = if (reviewCount == 0) 0.0 else 1.0,
                last_reviewed_at = if (reviewCount == 0) null else nextAt - 1,
                review_count = reviewCount.toLong(),
                ease_factor = 2.5,
            )
        }
    }
}
