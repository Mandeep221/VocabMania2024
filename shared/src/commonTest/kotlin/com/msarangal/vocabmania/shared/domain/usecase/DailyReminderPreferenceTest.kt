package com.msarangal.vocabmania.shared.domain.usecase

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.msarangal.vocabmania.shared.data.repository.SqlDelightUserSettingsRepository
import com.msarangal.vocabmania.shared.db.VocabManiaDatabase
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DailyReminderPreferenceTest {

    @Test
    fun preferenceDefaultsOffAndPersistsToggle() = runBlocking {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        VocabManiaDatabase.Schema.create(driver)
        val repository = SqlDelightUserSettingsRepository(VocabManiaDatabase(driver))
        val setEnabled = SetDailyReminderEnabledUseCase(repository)

        assertFalse(repository.getSettings().dailyReminderEnabled)

        setEnabled(true)
        assertTrue(repository.getSettings().dailyReminderEnabled)

        setEnabled(false)
        assertFalse(repository.getSettings().dailyReminderEnabled)
    }
}
