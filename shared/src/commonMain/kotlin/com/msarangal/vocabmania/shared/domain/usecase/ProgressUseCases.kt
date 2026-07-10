package com.msarangal.vocabmania.shared.domain.usecase

import com.msarangal.vocabmania.shared.domain.model.DailyActivity
import com.msarangal.vocabmania.shared.domain.model.DifficultyLevel
import com.msarangal.vocabmania.shared.domain.model.LevelProgress
import com.msarangal.vocabmania.shared.domain.model.ProgressDashboard
import com.msarangal.vocabmania.shared.domain.repository.ProgressRepository
import com.msarangal.vocabmania.shared.domain.repository.UserSettingsRepository

private const val MILLIS_PER_DAY = 86_400_000L
private const val ACTIVITY_WINDOW_DAYS = 7

class GetProgressDashboardUseCase(
    private val progressRepository: ProgressRepository,
    private val userSettingsRepository: UserSettingsRepository,
) {
    suspend operator fun invoke(nowEpochMillis: Long): ProgressDashboard {
        val settings = userSettingsRepository.getSettings()
        val todayEpochDay = nowEpochMillis / MILLIS_PER_DAY

        val levelProgress = DifficultyLevel.entries.associateWith { level ->
            buildLevelProgress(level, todayEpochDay)
        }

        return ProgressDashboard(
            selectedLevel = settings.selectedLevel,
            currentStreak = settings.currentStreak,
            longestStreak = settings.longestStreak,
            levelProgress = levelProgress,
        )
    }

    private suspend fun buildLevelProgress(
        level: DifficultyLevel,
        todayEpochDay: Long,
    ): LevelProgress {
        val reviewedCount = progressRepository.countReviewedCards(level)
        val matureCount = progressRepository.countMatureCards(level)

        val dailyActivity = (ACTIVITY_WINDOW_DAYS - 1 downTo 0).map { dayOffset ->
            val epochDay = todayEpochDay - dayOffset
            val dayStart = epochDay * MILLIS_PER_DAY
            val dayEnd = dayStart + MILLIS_PER_DAY
            val count = progressRepository.countReviewedOnDay(level, dayStart, dayEnd)
            DailyActivity(epochDay = epochDay, reviewCount = count.toInt())
        }
        val activityLast7Days = dailyActivity.sumOf { it.reviewCount.toLong() }

        return LevelProgress(
            level = level,
            masteryPercent = calculateMasteryPercent(matureCount, reviewedCount),
            matureCount = matureCount,
            reviewedCount = reviewedCount,
            activityLast7Days = activityLast7Days,
            dailyActivity = dailyActivity,
        )
    }

    companion object {
        fun calculateMasteryPercent(matureCount: Long, reviewedCount: Long): Int {
            if (reviewedCount == 0L) return 0
            return ((matureCount * 100.0) / reviewedCount).toInt().coerceIn(0, 100)
        }
    }
}
