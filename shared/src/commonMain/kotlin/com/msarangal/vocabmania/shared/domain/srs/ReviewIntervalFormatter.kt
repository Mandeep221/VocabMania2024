package com.msarangal.vocabmania.shared.domain.srs

import kotlin.math.roundToInt

object ReviewIntervalFormatter {
    fun formatNextReview(intervalDays: Double): String = when {
        intervalDays <= 0.0 -> "Up next now"
        intervalDays < 1.0 -> {
            val hours = (intervalDays * 24).roundToInt().coerceAtLeast(1)
            if (hours == 1) "Next in 1 hour" else "Next in $hours hours"
        }
        intervalDays < 1.5 -> "Next in 1 day"
        else -> "Next in ${intervalDays.roundToInt()} days"
    }

    fun formatCurrentInterval(intervalDays: Double): String? = when {
        intervalDays <= 0.0 -> null
        intervalDays < 1.5 -> "Interval: 1 day"
        else -> "Interval: ${intervalDays.roundToInt()} days"
    }
}
