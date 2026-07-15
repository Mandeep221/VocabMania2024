package com.msarangal.vocabmania.presentation.compose.components.materials

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.msarangal.vocabmania.presentation.compose.theme.VocabDimens
import com.msarangal.vocabmania.presentation.compose.theme.paperContentColors

/**
 * Quiet paper surface for editorial content (e.g. Word of the day).
 * Distinct from [PracticeHero] commitment fill and from utility chrome.
 */
@Composable
fun PaperContent(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = paperContentColors()
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = colors.container,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        border = BorderStroke(1.dp, colors.outline),
    ) {
        Column(
            modifier = Modifier.padding(VocabDimens.CardPadding),
            content = content,
        )
    }
}
