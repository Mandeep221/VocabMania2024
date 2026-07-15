package com.msarangal.vocabmania.presentation.compose.components.motion

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.msarangal.vocabmania.presentation.compose.theme.VocabMotion
import com.msarangal.vocabmania.presentation.compose.theme.rememberReduceMotion
import kotlinx.coroutines.delay

@Composable
fun EnterFadeSlide(
    visible: Boolean = true,
    modifier: Modifier = Modifier,
    delayIndex: Int = 0,
    content: @Composable () -> Unit,
) {
    val reduceMotion = rememberReduceMotion()
    var shown by remember { mutableStateOf(reduceMotion && visible) }
    LaunchedEffect(visible, reduceMotion) {
        if (reduceMotion) {
            shown = visible
        } else if (visible) {
            delay((delayIndex * 40L).coerceAtMost(200L))
            shown = true
        } else {
            shown = false
        }
    }

    AnimatedVisibility(
        visible = shown,
        modifier = modifier,
        enter = if (reduceMotion) {
            EnterTransition.None
        } else {
            fadeIn(animationSpec = VocabMotion.floatTween(false, VocabMotion.SlowMs)) +
                slideInVertically(animationSpec = VocabMotion.offsetTween(false, VocabMotion.SlowMs)) { it / 8 }
        },
        exit = if (reduceMotion) ExitTransition.None else fadeOut(),
    ) {
        content()
    }
}

@Composable
fun FadeReveal(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val reduceMotion = rememberReduceMotion()
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = if (reduceMotion) {
            EnterTransition.None
        } else {
            fadeIn(animationSpec = VocabMotion.floatTween(false)) +
                scaleIn(
                    animationSpec = VocabMotion.floatTween(false),
                    initialScale = 0.96f,
                )
        },
        exit = if (reduceMotion) ExitTransition.None else fadeOut(),
    ) {
        content()
    }
}

@Composable
fun <T> StepCrossfade(
    targetState: T,
    modifier: Modifier = Modifier,
    content: @Composable (T) -> Unit,
) {
    val reduceMotion = rememberReduceMotion()
    AnimatedContent(
        targetState = targetState,
        modifier = modifier,
        transitionSpec = {
            if (reduceMotion) {
                EnterTransition.None togetherWith ExitTransition.None
            } else {
                (
                    fadeIn(animationSpec = VocabMotion.floatTween(false)) +
                        slideInHorizontally(animationSpec = VocabMotion.offsetTween(false)) { it / 12 }
                    ) togetherWith (
                    fadeOut(animationSpec = VocabMotion.floatTween(false)) +
                        slideOutHorizontally(animationSpec = VocabMotion.offsetTween(false)) { -it / 12 }
                    )
            }
        },
        label = "stepCrossfade",
    ) { state ->
        content(state)
    }
}

@Composable
fun animateProgressFraction(target: Float): Float {
    val reduceMotion = rememberReduceMotion()
    val animated by animateFloatAsState(
        targetValue = target.coerceIn(0f, 1f),
        animationSpec = VocabMotion.floatTween(reduceMotion, VocabMotion.SlowMs),
        label = "progressFraction",
    )
    return animated
}

@Composable
fun rememberPressScale(
    interactionSource: MutableInteractionSource,
    enabled: Boolean = true,
): Float {
    val reduceMotion = rememberReduceMotion()
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (enabled && pressed && !reduceMotion) 0.96f else 1f,
        animationSpec = VocabMotion.floatSpring(reduceMotion),
        label = "pressScale",
    )
    return scale
}

@Composable
fun animateSettlingInt(target: Int): Int {
    val reduceMotion = rememberReduceMotion()
    var display by remember { mutableIntStateOf(if (reduceMotion) target else 0) }
    LaunchedEffect(target, reduceMotion) {
        if (reduceMotion) {
            display = target
            return@LaunchedEffect
        }
        display = 0
        delay(80)
        val step = if (target <= 0) 1 else (target / 8).coerceAtLeast(1)
        var current = 0
        while (current < target) {
            current = (current + step).coerceAtMost(target)
            display = current
            delay(36)
        }
        display = target
    }
    return display
}
