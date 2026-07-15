package com.msarangal.vocabmania.presentation.compose.onboarding

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.msarangal.vocabmania.presentation.compose.components.materials.PracticeHero
import com.msarangal.vocabmania.presentation.compose.components.motion.StepCrossfade
import com.msarangal.vocabmania.presentation.compose.theme.VocabDimens
import com.msarangal.vocabmania.presentation.compose.theme.paperContentColors
import com.msarangal.vocabmania.presentation.compose.theme.practiceHeroColors
import com.msarangal.vocabmania.shared.domain.model.DifficultyLevel

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel,
    onComplete: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val heroColors = practiceHeroColors()

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
            Column(modifier = Modifier.weight(1f)) {
                StepCrossfade(targetState = uiState.step) { step ->
                    when (step) {
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
                }

                uiState.errorMessage?.let { message ->
                    Spacer(modifier = Modifier.height(VocabDimens.SectionGap))
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
                colors = ButtonDefaults.buttonColors(
                    containerColor = heroColors.container,
                    contentColor = heroColors.onContainer,
                    disabledContainerColor = heroColors.container.copy(alpha = 0.45f),
                    disabledContentColor = heroColors.onContainer.copy(alpha = 0.7f),
                ),
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(20.dp),
                        strokeWidth = 2.dp,
                        color = heroColors.onContainer,
                    )
                } else {
                    Text(
                        text = when (uiState.step) {
                            OnboardingStep.WELCOME -> "Get started"
                            OnboardingStep.LEVEL -> "Continue"
                            OnboardingStep.DAILY_GOAL -> "Go to Today"
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun WelcomeStep() {
    val heroColors = practiceHeroColors()
    PracticeHero(
        title = "VocabMania",
        meta = "5 minutes a day. Words that stick.",
        supporting = {
            Text(
                text = "Build a daily practice habit. Open the app, do today’s bite, and keep a streak — words come back right when you’re about to forget them.",
                style = MaterialTheme.typography.bodyLarge,
                color = heroColors.onContainer,
                textAlign = TextAlign.Start,
            )
        },
    )
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
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(modifier = Modifier.height(VocabDimens.TightGap))
        Text(
            text = "We’ll tailor today’s practice to this level from the start.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(VocabDimens.SectionGap + VocabDimens.TightGap))
        SelectableOption(
            label = "Easy",
            description = "Everyday vocabulary",
            selected = selectedLevel == DifficultyLevel.EASY,
            onClick = { onLevelSelected(DifficultyLevel.EASY) },
        )
        Spacer(modifier = Modifier.height(VocabDimens.MediumGap))
        SelectableOption(
            label = "Medium",
            description = "Stronger word knowledge",
            selected = selectedLevel == DifficultyLevel.MEDIUM,
            onClick = { onLevelSelected(DifficultyLevel.MEDIUM) },
        )
        Spacer(modifier = Modifier.height(VocabDimens.MediumGap))
        SelectableOption(
            label = "Tough",
            description = "Advanced and rare words",
            selected = selectedLevel == DifficultyLevel.TOUGH,
            onClick = { onLevelSelected(DifficultyLevel.TOUGH) },
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
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(modifier = Modifier.height(VocabDimens.TightGap))
        Text(
            text = "How many words do you want to practice each day?",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(VocabDimens.SectionGap + VocabDimens.TightGap))
        options.forEach { goal ->
            SelectableOption(
                label = "$goal words per day",
                description = when (goal) {
                    10 -> "A gentle daily pace"
                    15 -> "Recommended for most learners"
                    else -> "A stronger daily push"
                },
                selected = selectedDailyGoal == goal,
                onClick = { onGoalSelected(goal) },
            )
            Spacer(modifier = Modifier.height(VocabDimens.MediumGap))
        }
    }
}

@Composable
private fun SelectableOption(
    label: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val paper = paperContentColors()
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton,
            ),
        shape = MaterialTheme.shapes.large,
        color = paper.container,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        border = if (selected) {
            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        } else {
            BorderStroke(1.dp, paper.outline)
        },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(VocabDimens.CardPadding),
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
}
