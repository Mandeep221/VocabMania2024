package com.msarangal.vocabmania.shared.domain.srs

import kotlin.math.roundToInt

object ReviewIntervalFormatter {
    fun formatNextReview(intervalDays: Double): String = when {
        intervalDays <= 0.0 -> "Next review now"
        intervalDays < 1.0 -> {
            val hours = (intervalDays * 24).roundToInt().coerceAtLeast(1)
            if (hours == 1) "Next review in 1 hour" else "Next review in $hours hours"
        }
        intervalDays < 1.5 -> "Next review in 1 day"
        else -> "Next review in ${intervalDays.roundToInt()} days"
    }

    fun formatCurrentInterval(intervalDays: Double): String? = when {
        intervalDays <= 0.0 -> null
        intervalDays < 1.5 -> "Interval: 1 day"
        else -> "Interval: ${intervalDays.roundToInt()} days"
    }
}
