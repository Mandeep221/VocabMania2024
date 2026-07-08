package com.msarangal.vocabmania.shared.data.migration

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.msarangal.vocabmania.shared.db.VocabManiaDatabase
import com.msarangal.vocabmania.shared.domain.model.DifficultyLevel
import com.msarangal.vocabmania.shared.domain.repository.MigrationRepository
import com.msarangal.vocabmania.shared.domain.repository.ReviewRepository
import com.msarangal.vocabmania.shared.domain.repository.WordRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * One-time import from legacy `favoritewords.db`.
 * Legacy SQLite stores favorites + revision lists — not the full Firebase word catalog.
 */
class LegacyDatabaseMigrator(
    private val context: Context,
    private val wordRepository: WordRepository,
    private val reviewRepository: ReviewRepository,
    private val migrationRepository: MigrationRepository,
) {
    suspend fun migrateIfNeeded(nowEpochMillis: Long) {
        if (migrationRepository.isMigrationComplete(MIGRATION_KEY)) return

        withContext(Dispatchers.IO) {
            val legacyPath = context.getDatabasePath(LEGACY_DB_NAME)
            if (!legacyPath.exists()) return@withContext

            val legacyDb = SQLiteDatabase.openDatabase(
                legacyPath.path,
                null,
                SQLiteDatabase.OPEN_READONLY,
            )

            try {
                migrateFavorites(legacyDb, nowEpochMillis)
                migrateRevision(legacyDb, "easy_revision", "easy_word", "easy_meaning", "easy_usage", DifficultyLevel.EASY, nowEpochMillis)
                migrateRevision(legacyDb, "medium_revision", "med_word", "med_meaning", "med_usage", DifficultyLevel.MEDIUM, nowEpochMillis)
                migrateRevision(legacyDb, "tough_revision", "tough_word", "tough_meaning", "tough_usage", DifficultyLevel.TOUGH, nowEpochMillis)
            } finally {
                legacyDb.close()
            }
        }

        migrationRepository.markMigrationComplete(MIGRATION_KEY, nowEpochMillis)
    }

    private suspend fun migrateFavorites(legacyDb: SQLiteDatabase, nowEpochMillis: Long) {
        legacyDb.rawQuery("SELECT word, meaning FROM favorites", null).use { cursor ->
            while (cursor.moveToNext()) {
                val text = cursor.getString(0)
                val meaning = cursor.getString(1)
                val wordId = wordRepository.insertWord(
                    text = text,
                    meaning = meaning,
                    usageExample = null,
                    level = DifficultyLevel.EASY,
                    isFavorite = true,
                )
                reviewRepository.ensureReviewCard(wordId, nowEpochMillis)
            }
        }
    }

    private suspend fun migrateRevision(
        legacyDb: SQLiteDatabase,
        table: String,
        wordColumn: String,
        meaningColumn: String,
        usageColumn: String,
        level: DifficultyLevel,
        nowEpochMillis: Long,
    ) {
        legacyDb.rawQuery(
            "SELECT $wordColumn, $meaningColumn, $usageColumn FROM $table",
            null,
        ).use { cursor ->
            while (cursor.moveToNext()) {
                val text = cursor.getString(0)
                val meaning = cursor.getString(1)
                val usage = cursor.getString(2)
                val wordId = wordRepository.insertWord(
                    text = text,
                    meaning = meaning,
                    usageExample = usage,
                    level = level,
                )
                reviewRepository.ensureReviewCard(wordId, nowEpochMillis)
            }
        }
    }

    companion object {
        const val LEGACY_DB_NAME = "favoritewords"
        const val MIGRATION_KEY = "legacy_sqlite_import"
    }
}
