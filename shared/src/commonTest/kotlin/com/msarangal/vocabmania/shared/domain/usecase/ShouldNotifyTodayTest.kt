package com.msarangal.vocabmania.shared.domain.usecase

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ShouldNotifyTodayTest {

    private val today = 20_000L

    @Test
    fun notifiesWhenDueCountAtLeastOne() {
        assertTrue(ShouldNotifyToday(dueCount = 1, lastSessionEpochDay = today, todayEpochDay = today))
        assertTrue(ShouldNotifyToday(dueCount = 3, lastSessionEpochDay = today - 1, todayEpochDay = today))
    }

    @Test
    fun notifiesWhenStreakAtRiskEvenIfNoDueWords() {
        assertTrue(ShouldNotifyToday(dueCount = 0, lastSessionEpochDay = today - 1, todayEpochDay = today))
        assertTrue(ShouldNotifyToday(dueCount = 0, lastSessionEpochDay = null, todayEpochDay = today))
    }

    @Test
    fun skipsWhenClearAndAlreadyPracticedToday() {
        assertFalse(ShouldNotifyToday(dueCount = 0, lastSessionEpochDay = today, todayEpochDay = today))
    }

    @Test
    fun reminderNotificationBodyMatchesPrdCopy() {
        assertEquals("2 words due — keep your streak going", reminderNotificationBody(2))
        assertEquals("Words are waiting", reminderNotificationBody(0))
    }
}
