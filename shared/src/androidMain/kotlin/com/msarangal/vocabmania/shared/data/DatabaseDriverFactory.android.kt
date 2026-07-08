package com.msarangal.vocabmania.shared.data

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.msarangal.vocabmania.shared.db.VocabManiaDatabase

actual class DatabaseDriverFactory(
    private val context: Context,
) {
    actual fun createDriver(): SqlDriver =
        AndroidSqliteDriver(
            schema = VocabManiaDatabase.Schema,
            context = context,
            name = "vocabmania.db",
        )
}
