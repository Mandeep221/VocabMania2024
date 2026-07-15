package com.msarangal.vocabmania.presentation.compose.sessioncomplete

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.msarangal.vocabmania.presentation.compose.components.materials.PracticeHero
import com.msarangal.vocabmania.presentation.compose.components.motion.EnterFadeSlide
import com.msarangal.vocabmania.presentation.compose.components.motion.animateSettlingInt
import com.msarangal.vocabmania.presentation.compose.theme.VocabDimens
import com.msarangal.vocabmania.presentation.compose.theme.practiceHeroColors

@Composable
fun SessionCompleteScreen(
    viewModel: SessionCompleteViewModel,
    onDone: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(VocabDimens.ScreenPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else {
                EnterFadeSlide {
                    PracticeCompleteHero(
                        practicedCount = uiState.reviewedCount,
                        currentStreak = uiState.currentStreak,
                        onBackToToday = onDone,
                    )
                }
                uiState.errorMessage?.let { message ->
                    Spacer(modifier = Modifier.height(VocabDimens.SectionGap))
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

@Composable
private fun PracticeCompleteHero(
    practicedCount: Int,
    currentStreak: Int,
    onBackToToday: () -> Unit,
) {
    val heroColors = practiceHeroColors()
    val settledStreak = animateSettlingInt(currentStreak)

    PracticeHero(
        title = "Practice complete",
        meta = "Nice work — keep the streak going.",
        supporting = {
            Text(
                text = practicedCountLabel(practicedCount),
                style = MaterialTheme.typography.headlineSmall,
                color = heroColors.onContainer,
            )
            Text(
                text = streakPayoffLabel(settledStreak),
                style = MaterialTheme.typography.titleMedium,
                color = heroColors.meta,
            )
        },
        action = {
            Button(
                onClick = onBackToToday,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = heroColors.onContainer,
                    contentColor = heroColors.container,
                ),
            ) {
                Text("Back to Today")
            }
        },
    )
}

private fun practicedCountLabel(count: Int): String =
    if (count == 1) "1 word practiced" else "$count words practiced"

private fun streakPayoffLabel(streak: Int): String =
    if (streak == 1) "1-day streak" else "$streak-day streak"
