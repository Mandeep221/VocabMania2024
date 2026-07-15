package com.msarangal.vocabmania.shared

import com.msarangal.vocabmania.shared.data.DatabaseDriverFactory
import com.msarangal.vocabmania.shared.data.catalog.NoOpWordCatalogRepository
import com.msarangal.vocabmania.shared.data.repository.CachedWordOfTheDayRepository
import com.msarangal.vocabmania.shared.data.repository.SqlDelightMigrationRepository
import com.msarangal.vocabmania.shared.data.repository.SqlDelightProgressRepository
import com.msarangal.vocabmania.shared.data.repository.SqlDelightReviewRepository
import com.msarangal.vocabmania.shared.data.repository.SqlDelightUserSettingsRepository
import com.msarangal.vocabmania.shared.data.repository.SqlDelightWordOfTheDayCache
import com.msarangal.vocabmania.shared.data.repository.SqlDelightWordRepository
import com.msarangal.vocabmania.shared.db.VocabManiaDatabase
import com.msarangal.vocabmania.shared.domain.repository.MigrationRepository
import com.msarangal.vocabmania.shared.domain.repository.ProgressRepository
import com.msarangal.vocabmania.shared.domain.repository.ReviewRepository
import com.msarangal.vocabmania.shared.domain.repository.UserSettingsRepository
import com.msarangal.vocabmania.shared.domain.repository.WordCatalogRepository
import com.msarangal.vocabmania.shared.domain.repository.WordOfTheDayRepository
import com.msarangal.vocabmania.shared.domain.repository.WordRepository
import com.msarangal.vocabmania.shared.domain.usecase.ApplyReviewRatingUseCase
import com.msarangal.vocabmania.shared.domain.usecase.CompleteOnboardingUseCase
import com.msarangal.vocabmania.shared.domain.usecase.CompleteReviewSessionUseCase
import com.msarangal.vocabmania.shared.domain.usecase.GetDueWordsUseCase
import com.msarangal.vocabmania.shared.domain.usecase.GetFavoritesUseCase
import com.msarangal.vocabmania.shared.domain.usecase.GetProgressDashboardUseCase
import com.msarangal.vocabmania.shared.domain.usecase.GetUserSettingsUseCase
import com.msarangal.vocabmania.shared.domain.usecase.GetWordCatalogStatusUseCase
import com.msarangal.vocabmania.shared.domain.usecase.GetWordOfTheDayUseCase
import com.msarangal.vocabmania.shared.domain.usecase.ImportWordCatalogUseCase
import com.msarangal.vocabmania.shared.domain.usecase.SaveUserSettingsUseCase
import com.msarangal.vocabmania.shared.domain.usecase.SetDailyReminderEnabledUseCase
import com.msarangal.vocabmania.shared.domain.usecase.ToggleFavoriteUseCase

class VocabManiaShared private constructor(
    private val database: VocabManiaDatabase,
    val wordRepository: WordRepository,
    val reviewRepository: ReviewRepository,
    val userSettingsRepository: UserSettingsRepository,
    val migrationRepository: MigrationRepository,
    val wordCatalogRepository: WordCatalogRepository,
    val progressRepository: ProgressRepository,
    val wordOfTheDayRepository: WordOfTheDayRepository,
) {
    val getDueWordsUseCase = GetDueWordsUseCase(reviewRepository, userSettingsRepository)
    val importWordCatalogUseCase = ImportWordCatalogUseCase(wordCatalogRepository)
    val getWordCatalogStatusUseCase = GetWordCatalogStatusUseCase(wordCatalogRepository)
    val applyReviewRatingUseCase = ApplyReviewRatingUseCase(reviewRepository)
    val completeReviewSessionUseCase = CompleteReviewSessionUseCase(userSettingsRepository)
    val getUserSettingsUseCase = GetUserSettingsUseCase(userSettingsRepository)
    val saveUserSettingsUseCase = SaveUserSettingsUseCase(userSettingsRepository)
    val setDailyReminderEnabledUseCase = SetDailyReminderEnabledUseCase(userSettingsRepository)
    val completeOnboardingUseCase = CompleteOnboardingUseCase(userSettingsRepository)
    val getProgressDashboardUseCase = GetProgressDashboardUseCase(progressRepository, userSettingsRepository)
    val getFavoritesUseCase = GetFavoritesUseCase(wordRepository)
    val toggleFavoriteUseCase = ToggleFavoriteUseCase(wordRepository)
    val getWordOfTheDayUseCase = GetWordOfTheDayUseCase(wordOfTheDayRepository)

    val databaseInstance: VocabManiaDatabase = database

    companion object {
        fun create(
            driverFactory: DatabaseDriverFactory,
            wordCatalogRepository: WordCatalogRepository? = null,
            wordOfTheDayRepository: WordOfTheDayRepository? = null,
        ): VocabManiaShared {
            val database = VocabManiaDatabase(driverFactory.createDriver())
            val wordRepository = SqlDelightWordRepository(database)
            val cache = SqlDelightWordOfTheDayCache(database)
            return create(
                database = database,
                wordRepository = wordRepository,
                reviewRepository = SqlDelightReviewRepository(database),
                userSettingsRepository = SqlDelightUserSettingsRepository(database),
                migrationRepository = SqlDelightMigrationRepository(database),
                wordCatalogRepository = wordCatalogRepository ?: NoOpWordCatalogRepository(wordRepository),
                progressRepository = SqlDelightProgressRepository(database),
                wordOfTheDayRepository = wordOfTheDayRepository ?: CachedWordOfTheDayRepository(cache),
            )
        }

        fun create(
            database: VocabManiaDatabase,
            wordRepository: WordRepository,
            reviewRepository: ReviewRepository,
            userSettingsRepository: UserSettingsRepository,
            migrationRepository: MigrationRepository,
            wordCatalogRepository: WordCatalogRepository,
            progressRepository: ProgressRepository,
            wordOfTheDayRepository: WordOfTheDayRepository,
        ): VocabManiaShared = VocabManiaShared(
            database = database,
            wordRepository = wordRepository,
            reviewRepository = reviewRepository,
            userSettingsRepository = userSettingsRepository,
            migrationRepository = migrationRepository,
            wordCatalogRepository = wordCatalogRepository,
            progressRepository = progressRepository,
            wordOfTheDayRepository = wordOfTheDayRepository,
        )
    }
}
