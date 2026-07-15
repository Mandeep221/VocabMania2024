package com.msarangal.vocabmania.shared.domain.usecase

import com.msarangal.vocabmania.shared.domain.repository.UserSettingsRepository

/**
 * Notify at fire time when there is work due, or when today's session is missing (streak at risk).
 * Skip only when the queue is clear and a session already completed today.
 */
object ShouldNotifyToday {
    operator fun invoke(
        dueCount: Int,
        lastSessionEpochDay: Long?,
        todayEpochDay: Long,
    ): Boolean {
        if (dueCount >= 1) return true
        return lastSessionEpochDay != todayEpochDay
    }
}

fun reminderNotificationBody(dueCount: Int): String =
    if (dueCount >= 1) {
        "$dueCount words due — keep your streak going"
    } else {
        "Words are waiting"
    }

class SetDailyReminderEnabledUseCase(
    private val userSettingsRepository: UserSettingsRepository,
) {
    suspend operator fun invoke(enabled: Boolean) {
        val current = userSettingsRepository.getSettings()
        userSettingsRepository.saveSettings(
            current.copy(dailyReminderEnabled = enabled),
        )
    }
}
