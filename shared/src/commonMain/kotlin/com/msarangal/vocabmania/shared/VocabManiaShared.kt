package com.msarangal.vocabmania.shared

import com.msarangal.vocabmania.shared.data.DatabaseDriverFactory
import com.msarangal.vocabmania.shared.data.catalog.NoOpWordCatalogRepository
import com.msarangal.vocabmania.shared.data.repository.SqlDelightMigrationRepository
import com.msarangal.vocabmania.shared.data.repository.SqlDelightProgressRepository
import com.msarangal.vocabmania.shared.data.repository.SqlDelightReviewRepository
import com.msarangal.vocabmania.shared.data.repository.SqlDelightUserSettingsRepository
import com.msarangal.vocabmania.shared.data.repository.SqlDelightWordRepository
import com.msarangal.vocabmania.shared.db.VocabManiaDatabase
import com.msarangal.vocabmania.shared.domain.repository.MigrationRepository
import com.msarangal.vocabmania.shared.domain.repository.ProgressRepository
import com.msarangal.vocabmania.shared.domain.repository.ReviewRepository
import com.msarangal.vocabmania.shared.domain.repository.UserSettingsRepository
import com.msarangal.vocabmania.shared.domain.repository.WordCatalogRepository
import com.msarangal.vocabmania.shared.domain.repository.WordRepository
import com.msarangal.vocabmania.shared.domain.usecase.ApplyReviewRatingUseCase
import com.msarangal.vocabmania.shared.domain.usecase.CompleteOnboardingUseCase
import com.msarangal.vocabmania.shared.domain.usecase.CompleteReviewSessionUseCase
import com.msarangal.vocabmania.shared.domain.usecase.GetDueWordsUseCase
import com.msarangal.vocabmania.shared.domain.usecase.GetWordCatalogStatusUseCase
import com.msarangal.vocabmania.shared.domain.usecase.GetProgressDashboardUseCase
import com.msarangal.vocabmania.shared.domain.usecase.GetUserSettingsUseCase
import com.msarangal.vocabmania.shared.domain.usecase.ImportWordCatalogUseCase
import com.msarangal.vocabmania.shared.domain.usecase.SaveUserSettingsUseCase

class VocabManiaShared private constructor(
    private val database: VocabManiaDatabase,
    val wordRepository: WordRepository,
    val reviewRepository: ReviewRepository,
    val userSettingsRepository: UserSettingsRepository,
    val migrationRepository: MigrationRepository,
    val wordCatalogRepository: WordCatalogRepository,
    val progressRepository: ProgressRepository,
) {
    val getDueWordsUseCase = GetDueWordsUseCase(reviewRepository, userSettingsRepository)
    val importWordCatalogUseCase = ImportWordCatalogUseCase(wordCatalogRepository)
    val getWordCatalogStatusUseCase = GetWordCatalogStatusUseCase(wordCatalogRepository)
    val applyReviewRatingUseCase = ApplyReviewRatingUseCase(reviewRepository)
    val completeReviewSessionUseCase = CompleteReviewSessionUseCase(userSettingsRepository)
    val getUserSettingsUseCase = GetUserSettingsUseCase(userSettingsRepository)
    val saveUserSettingsUseCase = SaveUserSettingsUseCase(userSettingsRepository)
    val completeOnboardingUseCase = CompleteOnboardingUseCase(userSettingsRepository)
    val getProgressDashboardUseCase = GetProgressDashboardUseCase(progressRepository, userSettingsRepository)

    val databaseInstance: VocabManiaDatabase = database

    companion object {
        fun create(
            driverFactory: DatabaseDriverFactory,
            wordCatalogRepository: WordCatalogRepository? = null,
        ): VocabManiaShared {
            val database = VocabManiaDatabase(driverFactory.createDriver())
            val wordRepository = SqlDelightWordRepository(database)
            return create(
                database = database,
                wordRepository = wordRepository,
                reviewRepository = SqlDelightReviewRepository(database),
                userSettingsRepository = SqlDelightUserSettingsRepository(database),
                migrationRepository = SqlDelightMigrationRepository(database),
                wordCatalogRepository = wordCatalogRepository ?: NoOpWordCatalogRepository(wordRepository),
                progressRepository = SqlDelightProgressRepository(database),
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
        ): VocabManiaShared = VocabManiaShared(
            database = database,
            wordRepository = wordRepository,
            reviewRepository = reviewRepository,
            userSettingsRepository = userSettingsRepository,
            migrationRepository = migrationRepository,
            wordCatalogRepository = wordCatalogRepository,
            progressRepository = progressRepository,
        )
    }
}
