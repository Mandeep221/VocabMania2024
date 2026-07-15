package com.msarangal.vocabmania.shared.domain.usecase

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.msarangal.vocabmania.shared.data.repository.SqlDelightProgressRepository
import com.msarangal.vocabmania.shared.data.repository.SqlDelightUserSettingsRepository
import com.msarangal.vocabmania.shared.db.VocabManiaDatabase
import com.msarangal.vocabmania.shared.domain.model.DifficultyLevel
import com.msarangal.vocabmania.shared.domain.progress.MATURE_INTERVAL_DAYS
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class GetProgressDashboardUseCaseTest {

    @Test
    fun calculateMasteryPercentReturnsZeroForEmptyState() {
        assertEquals(0, GetProgressDashboardUseCase.calculateMasteryPercent(0, 0))
        assertEquals(0, GetProgressDashboardUseCase.calculateMasteryPercent(0, 5))
    }

    @Test
    fun calculateMasteryPercentReturnsFullWhenAllMature() {
        assertEquals(100, GetProgressDashboardUseCase.calculateMasteryPercent(3, 3))
        assertEquals(50, GetProgressDashboardUseCase.calculateMasteryPercent(1, 2))
    }

    @Test
    fun dashboardDefaultsToSelectedLevelAndStreak() = runBlocking {
        val (useCase, database) = createUseCase()
        val now = 1_700_000_000_000L

        database.userSettingsQueries.insertDefaultSettings()
        database.userSettingsQueries.updateSettings(
            onboarding_complete = 1,
            selected_level = "M",
            daily_goal = 15,
            current_streak = 4,
            longest_streak = 7,
            last_session_epoch_day = now / 86_400_000L,
            daily_reminder_enabled = 0,
        )

        val dashboard = useCase(now)

        assertEquals(DifficultyLevel.MEDIUM, dashboard.selectedLevel)
        assertEquals(4, dashboard.currentStreak)
        assertEquals(7, dashboard.longestStreak)
    }

    @Test
    fun masteryPercentReflectsMatureCardsPerLevel() = runBlocking {
        val (useCase, database) = createUseCase()
        val now = 1_700_000_000_000L
        val today = now / 86_400_000L

        database.userSettingsQueries.insertDefaultSettings()
        database.userSettingsQueries.updateSettings(
            onboarding_complete = 1,
            selected_level = "E",
            daily_goal = 15,
            current_streak = 1,
            longest_streak = 1,
            last_session_epoch_day = today,
            daily_reminder_enabled = 0,
        )

        insertReviewedCard(database, text = "easy-mature", level = "E", intervalDays = MATURE_INTERVAL_DAYS, reviewedAt = now)
        insertReviewedCard(database, text = "easy-new", level = "E", intervalDays = 3.0, reviewedAt = now)
        insertReviewedCard(database, text = "medium-mature", level = "M", intervalDays = 30.0, reviewedAt = now)

        val dashboard = useCase(now)
        val easy = dashboard.levelProgress.getValue(DifficultyLevel.EASY)
        val medium = dashboard.levelProgress.getValue(DifficultyLevel.MEDIUM)
        val tough = dashboard.levelProgress.getValue(DifficultyLevel.TOUGH)

        assertEquals(50, easy.masteryPercent)
        assertEquals(1, easy.matureCount)
        assertEquals(2, easy.reviewedCount)
        assertEquals(100, medium.masteryPercent)
        assertEquals(1, medium.reviewedCount)
        assertEquals(0, tough.masteryPercent)
        assertEquals(0, tough.reviewedCount)
    }

    @Test
    fun activityLast7DaysCountsRecentReviewsPerLevel() = runBlocking {
        val (useCase, database) = createUseCase()
        val now = 1_700_000_000_000L
        val today = now / 86_400_000L
        val eightDaysAgo = now - (8 * 86_400_000L)

        database.userSettingsQueries.insertDefaultSettings()
        database.userSettingsQueries.updateSettings(
            onboarding_complete = 1,
            selected_level = "E",
            daily_goal = 15,
            current_streak = 2,
            longest_streak = 2,
            last_session_epoch_day = today,
            daily_reminder_enabled = 0,
        )

        insertReviewedCard(database, text = "recent-e", level = "E", intervalDays = 1.0, reviewedAt = now)
        insertReviewedCard(database, text = "old-e", level = "E", intervalDays = 1.0, reviewedAt = eightDaysAgo)
        insertReviewedCard(database, text = "recent-m", level = "M", intervalDays = 1.0, reviewedAt = now)

        val dashboard = useCase(now)
        val easy = dashboard.levelProgress.getValue(DifficultyLevel.EASY)
        val medium = dashboard.levelProgress.getValue(DifficultyLevel.MEDIUM)

        assertEquals(1, easy.activityLast7Days)
        assertEquals(1, medium.activityLast7Days)
        assertEquals(1, easy.dailyActivity.last().reviewCount)
        assertEquals(
            easy.activityLast7Days,
            easy.dailyActivity.sumOf { it.reviewCount.toLong() },
        )
    }

    private fun createUseCase(): Pair<GetProgressDashboardUseCase, VocabManiaDatabase> {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        VocabManiaDatabase.Schema.create(driver)
        val database = VocabManiaDatabase(driver)
        val progressRepository = SqlDelightProgressRepository(database)
        val userSettingsRepository = SqlDelightUserSettingsRepository(database)
        return GetProgressDashboardUseCase(progressRepository, userSettingsRepository) to database
    }

    private fun insertReviewedCard(
        database: VocabManiaDatabase,
        text: String,
        level: String,
        intervalDays: Double,
        reviewedAt: Long,
    ) {
        database.wordQueries.insertWord(
            text = text,
            meaning = "meaning",
            usage_example = null,
            level = level,
            is_favorite = 0,
            firebase_level_attempt = null,
        )
        val wordId = database.wordQueries.selectIdByTextAndLevel(text, level).executeAsOne()
        database.reviewCardQueries.insertReviewCard(
            word_id = wordId,
            next_review_at = reviewedAt + 86_400_000L,
            interval_days = intervalDays,
            last_reviewed_at = reviewedAt,
            review_count = 1,
            ease_factor = 2.5,
        )
    }
}
