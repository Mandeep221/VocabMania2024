package com.msarangal.vocabmania.presentation.compose.components.materials

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.msarangal.vocabmania.presentation.compose.theme.VocabDimens
import com.msarangal.vocabmania.presentation.compose.theme.practiceHeroColors

/**
 * Filled commitment surface for today’s practice action.
 * Light type on deep/luminous teal — not another beige content card.
 */
@Composable
fun PracticeHero(
    title: String,
    modifier: Modifier = Modifier,
    meta: String? = null,
    supporting: (@Composable ColumnScope.() -> Unit)? = null,
    action: (@Composable () -> Unit)? = null,
) {
    val colors = practiceHeroColors()
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(colors.container)
            .padding(VocabDimens.CardPadding),
        verticalArrangement = Arrangement.spacedBy(VocabDimens.TightGap),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = colors.onContainer,
        )
        if (meta != null) {
            Text(
                text = meta,
                style = MaterialTheme.typography.bodySmall,
                color = colors.meta,
            )
        }
        if (supporting != null) {
            Spacer(modifier = Modifier.height(VocabDimens.TightGap))
            supporting()
        }
        if (action != null) {
            Spacer(modifier = Modifier.height(VocabDimens.MediumGap))
            action()
        }
    }
}
