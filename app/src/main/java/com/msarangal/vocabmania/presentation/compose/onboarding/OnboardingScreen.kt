package com.msarangal.vocabmania.presentation.compose.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.msarangal.vocabmania.shared.domain.model.DifficultyLevel

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel,
    onComplete: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                when (uiState.step) {
                    OnboardingStep.WELCOME -> WelcomeStep()
                    OnboardingStep.LEVEL -> LevelStep(
                        selectedLevel = uiState.selectedLevel,
                        onLevelSelected = viewModel::selectLevel,
                    )
                    OnboardingStep.DAILY_GOAL -> DailyGoalStep(
                        selectedDailyGoal = uiState.selectedDailyGoal,
                        onGoalSelected = viewModel::selectDailyGoal,
                    )
                }

                uiState.errorMessage?.let { message ->
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }

            Button(
                onClick = {
                    when (uiState.step) {
                        OnboardingStep.WELCOME,
                        OnboardingStep.LEVEL,
                        -> viewModel.goToNextStep()
                        OnboardingStep.DAILY_GOAL -> viewModel.completeOnboarding(onComplete)
                    }
                },
                enabled = !uiState.isSaving,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(20.dp),
                        strokeWidth = 2.dp,
                    )
                } else {
                    Text(
                        text = when (uiState.step) {
                            OnboardingStep.WELCOME -> "Get started"
                            OnboardingStep.LEVEL -> "Continue"
                            OnboardingStep.DAILY_GOAL -> "Start learning"
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun WelcomeStep() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            text = "VocabMania",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "5 minutes a day. Words that stick.",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Build a daily habit with spaced repetition — words come back right when you're about to forget them.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Start,
        )
    }
}

@Composable
private fun LevelStep(
    selectedLevel: DifficultyLevel,
    onLevelSelected: (DifficultyLevel) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Pick your starting level",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "You can change this later in settings.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(24.dp))
        LevelOption(
            label = "Easy",
            description = "Everyday vocabulary",
            selected = selectedLevel == DifficultyLevel.EASY,
            onClick = { onLevelSelected(DifficultyLevel.EASY) },
        )
        Spacer(modifier = Modifier.height(12.dp))
        LevelOption(
            label = "Medium",
            description = "Stronger word knowledge",
            selected = selectedLevel == DifficultyLevel.MEDIUM,
            onClick = { onLevelSelected(DifficultyLevel.MEDIUM) },
        )
        Spacer(modifier = Modifier.height(12.dp))
        LevelOption(
            label = "Tough",
            description = "Advanced and rare words",
            selected = selectedLevel == DifficultyLevel.TOUGH,
            onClick = { onLevelSelected(DifficultyLevel.TOUGH) },
        )
    }
}

@Composable
private fun LevelOption(
    label: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton,
            )
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, style = MaterialTheme.typography.titleMedium)
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        RadioButton(
            selected = selected,
            onClick = null,
        )
    }
}

@Composable
private fun DailyGoalStep(
    selectedDailyGoal: Int,
    onGoalSelected: (Int) -> Unit,
) {
    val options = listOf(10, 15, 20)
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Set your daily goal",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "How many words do you want to review each day?",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(24.dp))
        options.forEach { goal ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = selectedDailyGoal == goal,
                        onClick = { onGoalSelected(goal) },
                        role = Role.RadioButton,
                    )
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(
                    selected = selectedDailyGoal == goal,
                    onClick = null,
                )
                Text(
                    text = "$goal words per day",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 8.dp),
                )
            }
        }
    }
}
