package com.msarangal.vocabmania.shared

import android.content.Context
import com.msarangal.vocabmania.shared.data.AndroidDatabaseDriverFactory
import com.msarangal.vocabmania.shared.data.catalog.FirebaseWordCatalogRepository
import com.msarangal.vocabmania.shared.data.firebase.FirebaseWordCatalogImporter
import com.msarangal.vocabmania.shared.data.migration.LegacyDatabaseMigrator
import com.msarangal.vocabmania.shared.data.repository.SqlDelightMigrationRepository
import com.msarangal.vocabmania.shared.data.repository.SqlDelightReviewRepository
import com.msarangal.vocabmania.shared.data.repository.SqlDelightUserSettingsRepository
import com.msarangal.vocabmania.shared.data.repository.SqlDelightWordRepository
import com.msarangal.vocabmania.shared.data.seed.SeedDataLoader
import com.msarangal.vocabmania.shared.db.VocabManiaDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

object SharedBootstrap {
    @Volatile
    private var shared: VocabManiaShared? = null

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @JvmStatic
    fun initialize(context: Context) {
        if (shared != null) return

        val appContext = context.applicationContext
        val driverFactory = AndroidDatabaseDriverFactory(appContext)
        val database = VocabManiaDatabase(driverFactory.createDriver())
        val wordRepository = SqlDelightWordRepository(database)
        val reviewRepository = SqlDelightReviewRepository(database)
        val userSettingsRepository = SqlDelightUserSettingsRepository(database)
        val migrationRepository = SqlDelightMigrationRepository(database)
        val seedDataLoader = SeedDataLoader(wordRepository, reviewRepository)
        val wordCatalogRepository = FirebaseWordCatalogRepository(
            importer = FirebaseWordCatalogImporter(wordRepository, reviewRepository),
            wordRepository = wordRepository,
            migrationRepository = migrationRepository,
            seedDataLoader = seedDataLoader,
        )
        val instance = VocabManiaShared.create(
            database = database,
            wordRepository = wordRepository,
            reviewRepository = reviewRepository,
            userSettingsRepository = userSettingsRepository,
            migrationRepository = migrationRepository,
            wordCatalogRepository = wordCatalogRepository,
        )
        shared = instance

        scope.launch {
            val now = System.currentTimeMillis()
            LegacyDatabaseMigrator(
                context = appContext,
                wordRepository = instance.wordRepository,
                reviewRepository = instance.reviewRepository,
                migrationRepository = instance.migrationRepository,
            ).migrateIfNeeded(now)

            instance.importWordCatalogUseCase(now)
        }
    }

    fun requireShared(): VocabManiaShared =
        shared ?: error("SharedBootstrap.initialize(context) must be called before accessing shared APIs.")
}
