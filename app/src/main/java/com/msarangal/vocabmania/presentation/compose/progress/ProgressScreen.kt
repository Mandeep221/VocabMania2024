package com.msarangal.vocabmania.presentation.compose.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.msarangal.vocabmania.presentation.compose.components.empty.EmptyIllustration
import com.msarangal.vocabmania.presentation.compose.components.empty.VocabEmptyState
import com.msarangal.vocabmania.presentation.compose.components.motion.EnterFadeSlide
import com.msarangal.vocabmania.presentation.compose.components.motion.animateProgressFraction
import com.msarangal.vocabmania.presentation.compose.theme.VocabDimens
import com.msarangal.vocabmania.presentation.compose.theme.vocabTopAppBarColors
import com.msarangal.vocabmania.shared.domain.model.DifficultyLevel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(
    viewModel: ProgressViewModel,
    onBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refresh()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val levels = listOf(
        DifficultyLevel.EASY to "Easy",
        DifficultyLevel.MEDIUM to "Medium",
        DifficultyLevel.TOUGH to "Tough",
    )
    val selectedIndex = levels.indexOfFirst { it.first == uiState.selectedLevel }.coerceAtLeast(0)
    val levelProgress = uiState.levelProgress[uiState.selectedLevel] ?: LevelProgressUi()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Progress") },
                colors = vocabTopAppBarColors(),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { innerPadding ->
        if (uiState.isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(VocabDimens.ScreenPadding)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(VocabDimens.SectionGap),
        ) {
            TabRow(selectedTabIndex = selectedIndex) {
                levels.forEachIndexed { index, (level, label) ->
                    Tab(
                        selected = selectedIndex == index,
                        onClick = { viewModel.selectLevel(level) },
                        text = { Text(label) },
                    )
                }
            }

            if (levelProgress.reviewedCount == 0) {
                EnterFadeSlide {
                    VocabEmptyState(
                        illustration = EmptyIllustration.PROGRESS,
                        title = "No progress yet",
                        body = "Start reviewing words at this level to build mastery.",
                    )
                }
            }

            EnterFadeSlide(delayIndex = 1) {
                MasteryCard(
                    masteryPercent = levelProgress.masteryPercent,
                    matureCount = levelProgress.matureCount,
                    reviewedCount = levelProgress.reviewedCount,
                )
            }

            EnterFadeSlide(delayIndex = 2) {
                ActivityCard(
                    activityLast7Days = levelProgress.activityLast7Days,
                    dailyActivity = levelProgress.dailyActivity,
                )
            }

            EnterFadeSlide(delayIndex = 3) {
                StreakCard(
                    currentStreak = uiState.currentStreak,
                    longestStreak = uiState.longestStreak,
                )
            }

            uiState.errorMessage?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Composable
private fun MasteryCard(
    masteryPercent: Int,
    matureCount: Int,
    reviewedCount: Int,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(VocabDimens.CardPadding),
            verticalArrangement = Arrangement.spacedBy(VocabDimens.MediumGap),
        ) {
            Text(
                text = "Mastery",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "$masteryPercent%",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.primary,
            )
            val animatedProgress = animateProgressFraction(masteryPercent / 100f)
            LinearProgressIndicator(
                progress = animatedProgress,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
            Text(
                text = if (reviewedCount == 0) {
                    "Review a few words to see mastery grow here."
                } else {
                    "$matureCount of $reviewedCount reviewed words are mature (21+ day interval)."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun ActivityCard(
    activityLast7Days: Int,
    dailyActivity: List<Int>,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(VocabDimens.CardPadding),
            verticalArrangement = Arrangement.spacedBy(VocabDimens.MediumGap),
        ) {
            Text(
                text = "Activity",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "$activityLast7Days words reviewed in the last 7 days",
                style = MaterialTheme.typography.headlineSmall,
            )
            ActivityDots(dailyActivity = dailyActivity)
            Text(
                text = "Each dot is one day — brighter means more reviews that day.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun ActivityDots(dailyActivity: List<Int>) {
    val maxCount = dailyActivity.maxOrNull()?.coerceAtLeast(1) ?: 1
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom,
    ) {
        dailyActivity.forEach { count ->
            val intensity = count.toFloat() / maxCount
            val dotSize = 12.dp + (20.dp * intensity)
            Box(
                modifier = Modifier
                    .size(dotSize)
                    .clip(CircleShape)
                    .background(
                        if (count > 0) {
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.4f + (0.6f * intensity))
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        },
                    ),
            )
        }
    }
}

@Composable
private fun StreakCard(
    currentStreak: Int,
    longestStreak: Int,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(VocabDimens.CardPadding),
            verticalArrangement = Arrangement.spacedBy(VocabDimens.SectionGap),
        ) {
            Text(
                text = "Streak",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            SummaryRow(label = "Current streak", value = "$currentStreak days")
            SummaryRow(label = "Longest streak", value = "$longestStreak days")
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}
