package com.msarangal.vocabmania.presentation.compose.review

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.msarangal.vocabmania.presentation.compose.components.empty.EmptyIllustration
import com.msarangal.vocabmania.presentation.compose.components.empty.VocabEmptyState
import com.msarangal.vocabmania.presentation.compose.components.motion.FadeReveal
import com.msarangal.vocabmania.presentation.compose.components.motion.rememberPressScale
import com.msarangal.vocabmania.presentation.compose.theme.VocabDimens
import com.msarangal.vocabmania.presentation.compose.theme.vocabTopAppBarColors
import com.msarangal.vocabmania.shared.domain.model.PracticeAction
import com.msarangal.vocabmania.shared.domain.srs.ReviewIntervalFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(
    viewModel: ReviewViewModel,
    onSessionComplete: (reviewedCount: Int, lastScheduleFeedback: String?) -> Unit,
    onBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentWord = uiState.words.getOrNull(uiState.currentIndex)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(if (uiState.favoritesOnly) "Practice favorites" else "Practice")
                },
                colors = vocabTopAppBarColors(),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (currentWord != null && !uiState.isEmpty && !uiState.isLoading) {
                        IconButton(onClick = viewModel::toggleFavorite) {
                            Icon(
                                imageVector = if (currentWord.isFavorite) {
                                    Icons.Filled.Favorite
                                } else {
                                    Icons.Filled.FavoriteBorder
                                },
                                contentDescription = if (currentWord.isFavorite) {
                                    "Remove from favorites"
                                } else {
                                    "Add to favorites"
                                },
                                tint = if (currentWord.isFavorite) {
                                    MaterialTheme.colorScheme.secondary
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                },
                            )
                        }
                    }
                },
            )
        },
    ) { innerPadding ->
        when {
            uiState.isLoading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    CircularProgressIndicator()
                }
            }
            uiState.isEmpty -> {
                EmptyPracticeState(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(VocabDimens.ScreenPadding),
                    favoritesOnly = uiState.favoritesOnly,
                    onBack = onBack,
                )
            }
            else -> {
                PracticeContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(VocabDimens.ScreenPadding),
                    word = currentWord!!,
                    currentIndex = uiState.currentIndex,
                    totalCount = uiState.words.size,
                    isMeaningRevealed = uiState.isMeaningRevealed,
                    isApplyingRating = uiState.isApplyingRating,
                    scheduleFeedback = uiState.scheduleFeedback,
                    errorMessage = uiState.errorMessage,
                    onRevealMeaning = viewModel::revealMeaning,
                    onRate = { action -> viewModel.rate(action, onSessionComplete) },
                )
            }
        }
    }
}

@Composable
private fun EmptyPracticeState(
    modifier: Modifier = Modifier,
    favoritesOnly: Boolean,
    onBack: () -> Unit,
) {
    VocabEmptyState(
        illustration = if (favoritesOnly) {
            EmptyIllustration.NO_FAVORITES_DUE
        } else {
            EmptyIllustration.CAUGHT_UP
        },
        title = if (favoritesOnly) "No favorites due" else "All caught up!",
        body = if (favoritesOnly) {
            "None of your favorited words are due right now. Favorite words during practice, then check back here."
        } else {
            "No words are due right now. Check back later."
        },
        modifier = modifier,
        actionLabel = "Back to Today",
        onAction = onBack,
    )
}

@Composable
private fun PracticeContent(
    modifier: Modifier = Modifier,
    word: ReviewWordUi,
    currentIndex: Int,
    totalCount: Int,
    isMeaningRevealed: Boolean,
    isApplyingRating: Boolean,
    scheduleFeedback: String?,
    errorMessage: String?,
    onRevealMeaning: () -> Unit,
    onRate: (PracticeAction) -> Unit,
) {
    Column(modifier = modifier) {
        Text(
            text = "${currentIndex + 1} of $totalCount",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        if (word.isNew) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "New word",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        } else {
            ReviewIntervalFormatter.formatCurrentInterval(word.intervalDays)?.let { intervalLabel ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = intervalLabel,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Spacer(modifier = Modifier.height(VocabDimens.SectionGap))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .then(
                    if (!isMeaningRevealed) {
                        Modifier.clickable(onClick = onRevealMeaning)
                    } else {
                        Modifier
                    },
                ),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(VocabDimens.ScreenPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = word.text,
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                )
                FadeReveal(visible = isMeaningRevealed) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Spacer(modifier = Modifier.height(VocabDimens.SectionGap + VocabDimens.TightGap))
                        Text(
                            text = word.meaning,
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center,
                        )
                        word.usageExample?.let { example ->
                            Spacer(modifier = Modifier.height(VocabDimens.SectionGap))
                            Text(
                                text = "\"$example\"",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
                if (!isMeaningRevealed) {
                    Spacer(modifier = Modifier.height(VocabDimens.SectionGap))
                    Text(
                        text = "Tap to reveal meaning",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        errorMessage?.let { message ->
            Spacer(modifier = Modifier.height(VocabDimens.MediumGap))
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        FadeReveal(visible = isMeaningRevealed) {
            Column {
                Spacer(modifier = Modifier.height(VocabDimens.SectionGap))
                PracticeActions(
                    isNew = word.isNew,
                    enabled = !isApplyingRating && scheduleFeedback == null,
                    onRate = onRate,
                )
                scheduleFeedback?.let { feedback ->
                    Spacer(modifier = Modifier.height(VocabDimens.MediumGap))
                    Text(
                        text = feedback,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                    )
                }
                if (isApplyingRating) {
                    Spacer(modifier = Modifier.height(VocabDimens.MediumGap))
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }
            }
        }
    }
}

@Composable
private fun PracticeActions(
    isNew: Boolean,
    enabled: Boolean,
    onRate: (PracticeAction) -> Unit,
) {
    if (isNew) {
        Column(verticalArrangement = Arrangement.spacedBy(VocabDimens.TightGap)) {
            Text(
                text = "Learn this word",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
            PracticeFilledButton(
                label = "Got it",
                enabled = enabled,
                onClick = { onRate(PracticeAction.GOT_IT) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
        return
    }

    Column(verticalArrangement = Arrangement.spacedBy(VocabDimens.TightGap)) {
        Text(
            text = "How well did you know it?",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(VocabDimens.TightGap),
        ) {
            PracticeOutlinedButton(
                label = "Missed",
                enabled = enabled,
                onClick = { onRate(PracticeAction.MISSED) },
                modifier = Modifier.weight(1f),
            )
            PracticeOutlinedButton(
                label = "Almost",
                enabled = enabled,
                onClick = { onRate(PracticeAction.ALMOST) },
                modifier = Modifier.weight(1f),
            )
            PracticeFilledButton(
                label = "Got it",
                enabled = enabled,
                onClick = { onRate(PracticeAction.GOT_IT) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun PracticeOutlinedButton(
    label: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val scale = rememberPressScale(interactionSource, enabled)
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
        },
        interactionSource = interactionSource,
    ) {
        Text(label)
    }
}

@Composable
private fun PracticeFilledButton(
    label: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val scale = rememberPressScale(interactionSource, enabled)
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
        },
        interactionSource = interactionSource,
    ) {
        Text(label)
    }
}
