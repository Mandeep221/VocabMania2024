package com.msarangal.vocabmania.presentation.compose.theme

import android.provider.Settings
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset

object VocabMotion {
    const val FastMs = 180
    const val MediumMs = 280
    const val SlowMs = 420

    fun floatTween(reduced: Boolean, durationMs: Int = MediumMs): FiniteAnimationSpec<Float> =
        if (reduced) tween(durationMillis = 0) else tween(durationMs, easing = FastOutSlowInEasing)

    fun offsetTween(reduced: Boolean, durationMs: Int = MediumMs): FiniteAnimationSpec<IntOffset> =
        if (reduced) tween(durationMillis = 0) else tween(durationMs, easing = FastOutSlowInEasing)

    fun floatSpring(reduced: Boolean): FiniteAnimationSpec<Float> =
        if (reduced) {
            tween(durationMillis = 0)
        } else {
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMediumLow,
            )
        }
}

/**
 * True when the system animator duration scale is 0 (reduced motion / animations off).
 */
@Composable
fun rememberReduceMotion(): Boolean {
    val context = LocalContext.current
    return remember(context) {
        runCatching {
            Settings.Global.getFloat(
                context.contentResolver,
                Settings.Global.ANIMATOR_DURATION_SCALE,
                1f,
            ) == 0f
        }.getOrDefault(false)
    }
}
