package com.msarangal.vocabmania.presentation.compose.review

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.msarangal.vocabmania.shared.domain.model.ReviewRating
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
        topBar = {
            TopAppBar(
                title = {
                    Text(if (uiState.favoritesOnly) "Review favorites" else "Review")
                },
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
                EmptyReviewState(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(24.dp),
                    favoritesOnly = uiState.favoritesOnly,
                    onBack = onBack,
                )
            }
            else -> {
                ReviewContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(24.dp),
                    word = currentWord!!,
                    currentIndex = uiState.currentIndex,
                    totalCount = uiState.words.size,
                    isMeaningRevealed = uiState.isMeaningRevealed,
                    isApplyingRating = uiState.isApplyingRating,
                    scheduleFeedback = uiState.scheduleFeedback,
                    errorMessage = uiState.errorMessage,
                    onRevealMeaning = viewModel::revealMeaning,
                    onRate = { rating -> viewModel.rate(rating, onSessionComplete) },
                )
            }
        }
    }
}

@Composable
private fun EmptyReviewState(
    modifier: Modifier = Modifier,
    favoritesOnly: Boolean,
    onBack: () -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = if (favoritesOnly) {
                "No favorites due"
            } else {
                "All caught up!"
            },
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (favoritesOnly) {
                "None of your favorited words are due right now. Favorite words during review, then check back here."
            } else {
                "No words are due right now. Check back later."
            },
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onBack) {
            Text("Back to home")
        }
    }
}

@Composable
private fun ReviewContent(
    modifier: Modifier = Modifier,
    word: ReviewWordUi,
    currentIndex: Int,
    totalCount: Int,
    isMeaningRevealed: Boolean,
    isApplyingRating: Boolean,
    scheduleFeedback: String?,
    errorMessage: String?,
    onRevealMeaning: () -> Unit,
    onRate: (ReviewRating) -> Unit,
) {
    Column(modifier = modifier) {
        Text(
            text = "${currentIndex + 1} of $totalCount",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        ReviewIntervalFormatter.formatCurrentInterval(word.intervalDays)?.let { intervalLabel ->
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = intervalLabel,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

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
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = word.text,
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
                if (isMeaningRevealed) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = word.meaning,
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center,
                    )
                    word.usageExample?.let { example ->
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "\"$example\"",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Tap to reveal meaning",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        errorMessage?.let { message ->
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        if (isMeaningRevealed) {
            Spacer(modifier = Modifier.height(16.dp))
            RatingButtons(
                enabled = !isApplyingRating && scheduleFeedback == null,
                onRate = onRate,
            )
            scheduleFeedback?.let { feedback ->
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = feedback,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
            }
            if (isApplyingRating) {
                Spacer(modifier = Modifier.height(12.dp))
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
        }
    }
}

@Composable
private fun RatingButtons(
    enabled: Boolean,
    onRate: (ReviewRating) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "How well did you know it?",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            RatingButton(
                label = "Again",
                modifier = Modifier.weight(1f),
                enabled = enabled,
                onClick = { onRate(ReviewRating.AGAIN) },
            )
            RatingButton(
                label = "Hard",
                modifier = Modifier.weight(1f),
                enabled = enabled,
                onClick = { onRate(ReviewRating.HARD) },
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            RatingButton(
                label = "Good",
                modifier = Modifier.weight(1f),
                enabled = enabled,
                onClick = { onRate(ReviewRating.GOOD) },
            )
            RatingButton(
                label = "Easy",
                modifier = Modifier.weight(1f),
                enabled = enabled,
                onClick = { onRate(ReviewRating.EASY) },
            )
        }
    }
}

@Composable
private fun RatingButton(
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
    ) {
        Text(label)
    }
}
