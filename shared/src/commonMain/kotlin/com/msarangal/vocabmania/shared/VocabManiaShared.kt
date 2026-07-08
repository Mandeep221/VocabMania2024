package com.msarangal.vocabmania.shared

import com.msarangal.vocabmania.shared.data.DatabaseDriverFactory
import com.msarangal.vocabmania.shared.data.repository.SqlDelightMigrationRepository
import com.msarangal.vocabmania.shared.data.repository.SqlDelightReviewRepository
import com.msarangal.vocabmania.shared.data.repository.SqlDelightUserSettingsRepository
import com.msarangal.vocabmania.shared.data.repository.SqlDelightWordRepository
import com.msarangal.vocabmania.shared.db.VocabManiaDatabase
import com.msarangal.vocabmania.shared.domain.repository.MigrationRepository
import com.msarangal.vocabmania.shared.domain.repository.ReviewRepository
import com.msarangal.vocabmania.shared.domain.repository.UserSettingsRepository
import com.msarangal.vocabmania.shared.domain.repository.WordRepository
import com.msarangal.vocabmania.shared.domain.usecase.ApplyReviewRatingUseCase
import com.msarangal.vocabmania.shared.domain.usecase.CompleteOnboardingUseCase
import com.msarangal.vocabmania.shared.domain.usecase.CompleteReviewSessionUseCase
import com.msarangal.vocabmania.shared.domain.usecase.GetDueWordsUseCase
import com.msarangal.vocabmania.shared.domain.usecase.GetUserSettingsUseCase
import com.msarangal.vocabmania.shared.domain.usecase.SaveUserSettingsUseCase

class VocabManiaShared(
    driverFactory: DatabaseDriverFactory,
) {
    private val database: VocabManiaDatabase = VocabManiaDatabase(driverFactory.createDriver())

    val wordRepository: WordRepository = SqlDelightWordRepository(database)
    val reviewRepository: ReviewRepository = SqlDelightReviewRepository(database)
    val userSettingsRepository: UserSettingsRepository = SqlDelightUserSettingsRepository(database)
    val migrationRepository: MigrationRepository = SqlDelightMigrationRepository(database)

    val getDueWordsUseCase = GetDueWordsUseCase(reviewRepository, userSettingsRepository)
    val applyReviewRatingUseCase = ApplyReviewRatingUseCase(reviewRepository)
    val completeReviewSessionUseCase = CompleteReviewSessionUseCase(userSettingsRepository)
    val getUserSettingsUseCase = GetUserSettingsUseCase(userSettingsRepository)
    val saveUserSettingsUseCase = SaveUserSettingsUseCase(userSettingsRepository)
    val completeOnboardingUseCase = CompleteOnboardingUseCase(userSettingsRepository)

    val databaseInstance: VocabManiaDatabase = database
}
