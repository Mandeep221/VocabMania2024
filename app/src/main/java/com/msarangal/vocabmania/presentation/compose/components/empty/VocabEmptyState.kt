package com.msarangal.vocabmania.presentation.compose.components.empty

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.msarangal.vocabmania.presentation.compose.theme.VocabDimens

@Composable
fun VocabEmptyState(
    illustration: EmptyIllustration,
    title: String,
    body: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = VocabDimens.SectionGap),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        BookishEmptyIllustration(type = illustration)
        Spacer(modifier = Modifier.height(VocabDimens.SectionGap))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(modifier = Modifier.height(VocabDimens.TightGap))
        Text(
            text = body,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = VocabDimens.MediumGap),
        )
        if (actionLabel != null && onAction != null) {
            Spacer(modifier = Modifier.height(VocabDimens.SectionGap + VocabDimens.TightGap))
            Button(onClick = onAction) {
                Text(actionLabel)
            }
        }
    }
}
