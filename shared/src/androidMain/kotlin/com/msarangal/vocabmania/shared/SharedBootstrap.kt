package com.msarangal.vocabmania.shared

import android.content.Context
import com.msarangal.vocabmania.shared.data.DatabaseDriverFactory
import com.msarangal.vocabmania.shared.data.migration.LegacyDatabaseMigrator
import com.msarangal.vocabmania.shared.data.seed.SeedDataLoader
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
        val instance = VocabManiaShared(DatabaseDriverFactory(appContext))
        shared = instance

        scope.launch {
            val now = System.currentTimeMillis()
            LegacyDatabaseMigrator(
                context = appContext,
                wordRepository = instance.wordRepository,
                reviewRepository = instance.reviewRepository,
                migrationRepository = instance.migrationRepository,
            ).migrateIfNeeded(now)

            SeedDataLoader(
                wordRepository = instance.wordRepository,
                reviewRepository = instance.reviewRepository,
            ).seedIfEmpty(now)
        }
    }

    fun requireShared(): VocabManiaShared =
        shared ?: error("SharedBootstrap.initialize(context) must be called before accessing shared APIs.")
}
