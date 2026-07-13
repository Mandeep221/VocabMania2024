package com.msarangal.vocabmania.presentation.compose.home

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.msarangal.vocabmania.presentation.activity.TestActivity
import com.msarangal.vocabmania.shared.domain.model.WordCatalogImportState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onStartReview: () -> Unit,
    onReviewFavorites: () -> Unit,
    onOpenProgress: () -> Unit,
    onOpenFavorites: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var menuExpanded by remember { mutableStateOf(false) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refresh()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Today") },
                actions = {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                    ) {
                        DropdownMenuItem(
                            text = { Text("Favorites") },
                            onClick = {
                                menuExpanded = false
                                onOpenFavorites()
                            },
                        )
                        DropdownMenuItem(
                            text = { Text("Legacy app") },
                            onClick = {
                                menuExpanded = false
                                context.startActivity(Intent(context, TestActivity::class.java))
                            },
                        )
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
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "Daily review",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "${uiState.selectedLevelLabel} level · goal ${uiState.dailyGoal} words/day",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            RowStatCards(
                streak = uiState.currentStreak,
                dueCount = uiState.dueCount,
                totalWordCount = uiState.totalWordCount,
            )

            WordOfTheDaySection(
                wordOfTheDay = uiState.wordOfTheDay,
                isLoading = uiState.isWordOfTheDayLoading,
            )

            ProgressEntryCard(onClick = onOpenProgress)

            CatalogStatusMessage(importState = uiState.catalogImportState)

            uiState.errorMessage?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            if (uiState.dueCount == 0) {
                Text(
                    text = "You're all caught up for now. Words will appear here when they're due again.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onStartReview,
                enabled = uiState.dueCount > 0,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = if (uiState.dueCount > 0) {
                        "Start review (${uiState.dueCount} due)"
                    } else {
                        "No words due right now"
                    },
                )
            }

            OutlinedButton(
                onClick = onReviewFavorites,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = if (uiState.favoriteDueCount > 0) {
                        "Review favorites (${uiState.favoriteDueCount} due)"
                    } else {
                        "Review favorites"
                    },
                )
            }
        }
    }
}

@Composable
private fun WordOfTheDaySection(
    wordOfTheDay: WordOfTheDayUi?,
    isLoading: Boolean,
) {
    when {
        isLoading && wordOfTheDay == null -> {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "Word of the day",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    CircularProgressIndicator()
                }
            }
        }
        wordOfTheDay != null -> {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Word of the day",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = wordOfTheDay.word,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = wordOfTheDay.meaning,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    wordOfTheDay.usageExample?.let { usage ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "\"$usage\"",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    if (wordOfTheDay.isFromCache) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Showing cached word — connect for a fresh one.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
        // Hidden: offline cold start with no cache — leave Home intact.
        else -> Unit
    }
}

@Composable
private fun RowStatCards(
    streak: Int,
    dueCount: Int,
    totalWordCount: Int,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        StatCard(title = "Current streak", value = "$streak days")
        StatCard(title = "Due today", value = dueCount.toString())
        StatCard(title = "Words in library", value = totalWordCount.toString())
    }
}

@Composable
private fun CatalogStatusMessage(importState: WordCatalogImportState) {
    when (importState) {
        WordCatalogImportState.IMPORTING,
        WordCatalogImportState.PENDING,
        -> {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                Text(
                    text = "Importing vocabulary library…",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        WordCatalogImportState.FAILED -> {
            Text(
                text = "Using offline catalog. Connect to the internet and restart to download the full library.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        WordCatalogImportState.COMPLETE -> Unit
    }
}

@Composable
private fun ProgressEntryCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Your progress",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Mastery, activity, and streak",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}
