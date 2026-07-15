package com.msarangal.vocabmania.shared.domain.reminder

/**
 * Platform-owned scheduler for the opt-in 7 PM local reminder.
 * Preference persistence lives in :shared; permission + alarms live on Android.
 */
interface DailyReminderScheduler {
    fun scheduleDaily(hour: Int = 19, minute: Int = 0)

    fun cancel()
}
