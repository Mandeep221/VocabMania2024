package com.msarangal.vocabmania.presentation.reminder

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.msarangal.vocabmania.shared.SharedBootstrap
import com.msarangal.vocabmania.shared.domain.usecase.ShouldNotifyToday
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DailyReminderWorker(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            SharedBootstrap.initialize(applicationContext)
            val shared = SharedBootstrap.requireShared()
            val settings = shared.getUserSettingsUseCase()
            if (!settings.dailyReminderEnabled) {
                Log.d(TAG, "Reminder disabled — skipping")
                return@withContext Result.success()
            }

            val now = System.currentTimeMillis()
            val dueCount = shared.getDueWordsUseCase.countDue(now).toInt()
            val today = now / MILLIS_PER_DAY
            if (ShouldNotifyToday(dueCount, settings.lastSessionEpochDay, today)) {
                ReminderNotificationHelper.show(applicationContext, dueCount)
            } else {
                Log.d(TAG, "ShouldNotifyToday=false — no notification")
            }

            AndroidDailyReminderScheduler(applicationContext).scheduleDaily()
            Result.success()
        } catch (error: Exception) {
            Log.e(TAG, "Daily reminder failed", error)
            Result.retry()
        }
    }

    companion object {
        private const val TAG = "DailyReminderWorker"
        private const val MILLIS_PER_DAY = 86_400_000L
    }
}
