package com.msarangal.vocabmania.presentation.compose.sessioncomplete

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.msarangal.vocabmania.presentation.compose.components.motion.EnterFadeSlide
import com.msarangal.vocabmania.presentation.compose.components.motion.animateSettlingInt
import com.msarangal.vocabmania.presentation.compose.theme.VocabDimens

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
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator()
                } else {
                    EnterFadeSlide {
                        Text(
                            text = "Session complete!",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center,
                        )
                    }
                    Spacer(modifier = Modifier.height(VocabDimens.TightGap))
                    EnterFadeSlide(delayIndex = 1) {
                        Text(
                            text = "Nice work — keep the streak going.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                        )
                    }
                    Spacer(modifier = Modifier.height(VocabDimens.SectionGap * 2))
                    EnterFadeSlide(delayIndex = 2) {
                        SummaryCard(
                            reviewedCount = uiState.reviewedCount,
                            currentStreak = uiState.currentStreak,
                            longestStreak = uiState.longestStreak,
                        )
                    }
                    uiState.lastScheduleFeedback?.let { feedback ->
                        Spacer(modifier = Modifier.height(VocabDimens.SectionGap))
                        EnterFadeSlide(delayIndex = 3) {
                            Text(
                                text = feedback,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.secondary,
                                textAlign = TextAlign.Center,
                            )
                        }
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

            Button(
                onClick = onDone,
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Back to home")
            }
        }
    }
}

@Composable
private fun SummaryCard(
    reviewedCount: Int,
    currentStreak: Int,
    longestStreak: Int,
) {
    val settledStreak = animateSettlingInt(currentStreak)
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(VocabDimens.CardPadding),
            verticalArrangement = Arrangement.spacedBy(VocabDimens.SectionGap),
        ) {
            SummaryRow(label = "Words reviewed", value = reviewedCount.toString())
            SummaryRow(
                label = "Current streak",
                value = "$settledStreak days",
                highlight = true,
            )
            SummaryRow(label = "Longest streak", value = "$longestStreak days")
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    highlight: Boolean = false,
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
            color = if (highlight) {
                MaterialTheme.colorScheme.secondary
            } else {
                MaterialTheme.colorScheme.primary
            },
        )
    }
}
