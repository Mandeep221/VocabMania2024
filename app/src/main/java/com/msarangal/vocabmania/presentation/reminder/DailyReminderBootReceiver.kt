package com.msarangal.vocabmania.presentation.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.msarangal.vocabmania.shared.SharedBootstrap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Restores the 7 PM OneTimeWork after reboot when the preference is still enabled.
 */
class DailyReminderBootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != Intent.ACTION_BOOT_COMPLETED) return
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                SharedBootstrap.initialize(context.applicationContext)
                val settings = SharedBootstrap.requireShared().getUserSettingsUseCase()
                if (settings.dailyReminderEnabled) {
                    AndroidDailyReminderScheduler(context).scheduleDaily()
                    Log.d(TAG, "Rescheduled daily reminder after boot")
                }
            } catch (error: Exception) {
                Log.e(TAG, "Failed to restore daily reminder after boot", error)
            } finally {
                pendingResult.finish()
            }
        }
    }

    companion object {
        private const val TAG = "DailyReminderBoot"
    }
}
