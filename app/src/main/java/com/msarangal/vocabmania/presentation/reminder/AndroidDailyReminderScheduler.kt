package com.msarangal.vocabmania.presentation.reminder

import android.content.Context
import android.util.Log
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.msarangal.vocabmania.shared.domain.reminder.DailyReminderScheduler
import java.util.Calendar
import java.util.concurrent.TimeUnit

/**
 * Schedules a doze-friendly OneTimeWork until the next local 7 PM, then the worker reschedules.
 */
class AndroidDailyReminderScheduler(
    context: Context,
) : DailyReminderScheduler {

    private val appContext = context.applicationContext

    override fun scheduleDaily(hour: Int, minute: Int) {
        ReminderNotificationHelper.ensureChannel(appContext)
        val delayMillis = millisUntilNext(hour, minute)
        val request = OneTimeWorkRequestBuilder<DailyReminderWorker>()
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .addTag(WORK_TAG)
            .build()
        WorkManager.getInstance(appContext).enqueueUniqueWork(
            UNIQUE_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            request,
        )
        Log.d(TAG, "Scheduled daily reminder in ${delayMillis}ms (hour=$hour minute=$minute)")
    }

    override fun cancel() {
        WorkManager.getInstance(appContext).cancelUniqueWork(UNIQUE_WORK_NAME)
        Log.d(TAG, "Cancelled daily reminder")
    }

    companion object {
        private const val TAG = "DailyReminderScheduler"
        const val UNIQUE_WORK_NAME = "vocabmania_daily_reminder"
        private const val WORK_TAG = "daily_reminder"

        fun millisUntilNext(hour: Int, minute: Int, nowMillis: Long = System.currentTimeMillis()): Long {
            val calendar = Calendar.getInstance().apply {
                timeInMillis = nowMillis
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                if (timeInMillis <= nowMillis) {
                    add(Calendar.DAY_OF_YEAR, 1)
                }
            }
            return calendar.timeInMillis - nowMillis
        }
    }
}
