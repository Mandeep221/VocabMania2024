package com.msarangal.vocabmania.presentation.compose.favorites

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.msarangal.vocabmania.presentation.compose.components.empty.EmptyIllustration
import com.msarangal.vocabmania.presentation.compose.components.empty.VocabEmptyState
import com.msarangal.vocabmania.presentation.compose.components.materials.UtilityChevron
import com.msarangal.vocabmania.presentation.compose.components.materials.UtilityRow
import com.msarangal.vocabmania.presentation.compose.theme.VocabDimens
import com.msarangal.vocabmania.presentation.compose.theme.vocabTopAppBarColors

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel,
    onBack: () -> Unit,
    onPracticeFavorites: () -> Unit,
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

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Favorites") },
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
                .padding(horizontal = VocabDimens.ScreenPadding),
        ) {
            if (uiState.favoriteDueCount > 0) {
                UtilityRow(
                    title = "Practice favorites",
                    subtitle = "${uiState.favoriteDueCount} due",
                    onClick = onPracticeFavorites,
                    trailing = { UtilityChevron(contentDescription = "Practice favorites") },
                )
                Spacer(modifier = Modifier.height(VocabDimens.TightGap))
            } else {
                Spacer(modifier = Modifier.height(VocabDimens.TightGap))
            }

            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("Search favorites") },
                placeholder = { Text("Type a word") },
            )
            Spacer(modifier = Modifier.height(VocabDimens.MediumGap))

            uiState.errorMessage?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(modifier = Modifier.height(VocabDimens.TightGap))
            }

            val filtered = uiState.filteredFavorites
            when {
                uiState.favorites.isEmpty() -> {
                    VocabEmptyState(
                        illustration = EmptyIllustration.NO_FAVORITES,
                        title = "No favorites yet",
                        body = "Tap the heart on a word during practice to save it here.",
                        modifier = Modifier.padding(top = VocabDimens.SectionGap),
                    )
                }
                filtered.isEmpty() -> {
                    VocabEmptyState(
                        illustration = EmptyIllustration.NO_FAVORITES,
                        title = "No matches",
                        body = "Try a different search.",
                        modifier = Modifier.padding(top = VocabDimens.SectionGap),
                    )
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = VocabDimens.ScreenPadding),
                    ) {
                        itemsIndexed(filtered, key = { _, word -> word.id }) { index, word ->
                            FavoriteWordRow(
                                word = word,
                                showDivider = index < filtered.lastIndex,
                                modifier = Modifier.animateItemPlacement(),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FavoriteWordRow(
    word: FavoriteWordUi,
    showDivider: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(vertical = VocabDimens.MediumGap),
            verticalArrangement = Arrangement.spacedBy(VocabDimens.TightGap),
        ) {
            Text(
                text = word.text,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = word.meaning,
                style = MaterialTheme.typography.bodyLarge,
            )
            word.usageExample?.let { example ->
                Text(
                    text = "\"$example\"",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        if (showDivider) {
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
        }
    }
}
