package com.msarangal.vocabmania.presentation.compose.home

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Switch
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.animation.core.animateFloatAsState
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.msarangal.vocabmania.presentation.compose.components.empty.EmptyIllustration
import com.msarangal.vocabmania.presentation.compose.components.empty.VocabEmptyState
import com.msarangal.vocabmania.presentation.compose.components.motion.EnterFadeSlide
import com.msarangal.vocabmania.presentation.compose.theme.VocabDimens
import com.msarangal.vocabmania.presentation.compose.theme.VocabMotion
import com.msarangal.vocabmania.presentation.compose.theme.rememberReduceMotion
import com.msarangal.vocabmania.presentation.compose.theme.vocabTopAppBarColors
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

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) {
            viewModel.setDailyReminderEnabled(true)
        } else {
            viewModel.revertReminderOff()
        }
    }

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
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Today") },
                colors = vocabTopAppBarColors(),
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
                            text = { Text("Share") },
                            onClick = {
                                menuExpanded = false
                                shareApp(context)
                            },
                        )
                        DropdownMenuItem(
                            text = { Text("Rate") },
                            onClick = {
                                menuExpanded = false
                                rateApp(context)
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
                .padding(VocabDimens.ScreenPadding)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(VocabDimens.SectionGap),
        ) {
            Text(
                text = "Daily review",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = "${uiState.selectedLevelLabel} level · goal ${uiState.dailyGoal} words/day",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            EnterFadeSlide(delayIndex = 0) {
                RowStatCards(
                    streak = uiState.currentStreak,
                    dueCount = uiState.dueCount,
                    totalWordCount = uiState.totalWordCount,
                )
            }

            EnterFadeSlide(delayIndex = 1) {
                DailyReminderRow(
                    enabled = uiState.dailyReminderEnabled,
                    updating = uiState.isReminderUpdating,
                    onToggle = { wantEnabled ->
                        if (!wantEnabled) {
                            viewModel.setDailyReminderEnabled(false)
                            return@DailyReminderRow
                        }
                        if (needsNotificationPermission(context)) {
                            viewModel.markReminderPendingPermission()
                            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            viewModel.setDailyReminderEnabled(true)
                        }
                    },
                )
            }

            EnterFadeSlide(delayIndex = 2) {
                WordOfTheDaySection(
                    wordOfTheDay = uiState.wordOfTheDay,
                    isLoading = uiState.isWordOfTheDayLoading,
                )
            }

            EnterFadeSlide(delayIndex = 3) {
                ProgressEntryCard(onClick = onOpenProgress)
            }

            CatalogStatusMessage(importState = uiState.catalogImportState)

            uiState.errorMessage?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            if (uiState.dueCount == 0) {
                EnterFadeSlide(delayIndex = 4) {
                    VocabEmptyState(
                        illustration = EmptyIllustration.CAUGHT_UP,
                        title = "All caught up",
                        body = "You're clear for now. Words will appear here when they're due again.",
                    )
                }
            }

            Spacer(modifier = Modifier.height(VocabDimens.TightGap))

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
private fun DailyReminderRow(
    enabled: Boolean,
    updating: Boolean,
    onToggle: (Boolean) -> Unit,
) {
    val reduceMotion = rememberReduceMotion()
    val switchScale by animateFloatAsState(
        targetValue = if (enabled && !reduceMotion) 1.06f else 1f,
        animationSpec = VocabMotion.floatSpring(reduceMotion),
        label = "reminderToggleScale",
    )
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = VocabDimens.CardPadding, vertical = VocabDimens.MediumGap),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(VocabDimens.MediumGap),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Remind me daily at 7 PM",
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Local reminder when words are due or your streak is at risk.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Switch(
                checked = enabled,
                onCheckedChange = onToggle,
                enabled = !updating,
                modifier = Modifier.scale(switchScale),
            )
        }
    }
}

private fun needsNotificationPermission(context: Context): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return false
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.POST_NOTIFICATIONS,
    ) != PackageManager.PERMISSION_GRANTED
}

private fun playStoreWebUrl(packageName: String): String =
    "https://play.google.com/store/apps/details?id=$packageName"

private fun shareApp(context: Context) {
    val storeUrl = playStoreWebUrl(context.packageName)
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(
            Intent.EXTRA_TEXT,
            "Build a daily vocab habit with VocabMania.\n$storeUrl",
        )
    }
    context.startActivity(Intent.createChooser(sendIntent, "Share VocabMania"))
}

private fun rateApp(context: Context) {
    val packageName = context.packageName
    try {
        context.startActivity(
            Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")),
        )
    } catch (_: ActivityNotFoundException) {
        context.startActivity(
            Intent(Intent.ACTION_VIEW, Uri.parse(playStoreWebUrl(packageName))),
        )
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
                        .padding(VocabDimens.CardPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "Word of the day",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(modifier = Modifier.height(VocabDimens.SectionGap))
                    CircularProgressIndicator()
                }
            }
        }
        wordOfTheDay != null -> {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(VocabDimens.CardPadding)) {
                    Text(
                        text = "Word of the day",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(VocabDimens.TightGap))
                    Text(
                        text = wordOfTheDay.word,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(modifier = Modifier.height(VocabDimens.TightGap))
                    Text(
                        text = wordOfTheDay.meaning,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    wordOfTheDay.usageExample?.let { usage ->
                        Spacer(modifier = Modifier.height(VocabDimens.TightGap))
                        Text(
                            text = "\"$usage\"",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    if (wordOfTheDay.isFromCache) {
                        Spacer(modifier = Modifier.height(VocabDimens.TightGap))
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
    Column(verticalArrangement = Arrangement.spacedBy(VocabDimens.MediumGap)) {
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
        Column(modifier = Modifier.padding(VocabDimens.CardPadding)) {
            Text(
                text = "Your progress",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(VocabDimens.TightGap))
            Text(
                text = "Mastery, activity, and streak",
                style = MaterialTheme.typography.titleMedium,
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
        Column(modifier = Modifier.padding(VocabDimens.CardPadding)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(VocabDimens.TightGap))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}
