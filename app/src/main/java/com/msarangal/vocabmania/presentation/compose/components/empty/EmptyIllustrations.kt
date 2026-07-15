package com.msarangal.vocabmania.presentation.compose.components.empty

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class EmptyIllustration {
    CAUGHT_UP,
    NO_FAVORITES,
    NO_FAVORITES_DUE,
    PROGRESS,
}

@Composable
fun BookishEmptyIllustration(
    type: EmptyIllustration,
    modifier: Modifier = Modifier,
    size: Dp = 140.dp,
) {
    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary
    val muted = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f)
    val surface = MaterialTheme.colorScheme.surface

    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        when (type) {
            EmptyIllustration.CAUGHT_UP -> drawOpenBook(w, h, primary, secondary, surface, muted)
            EmptyIllustration.NO_FAVORITES -> drawBookmarkBook(w, h, primary, secondary, muted)
            EmptyIllustration.NO_FAVORITES_DUE -> drawEmptyShelf(w, h, primary, muted, secondary)
            EmptyIllustration.PROGRESS -> drawGrowthPath(w, h, primary, secondary, muted)
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawOpenBook(
    w: Float,
    h: Float,
    primary: Color,
    secondary: Color,
    page: Color,
    muted: Color,
) {
    val bookTop = h * 0.28f
    val bookBottom = h * 0.78f
    val mid = w * 0.5f
    val left = w * 0.14f
    val right = w * 0.86f

    // Spine shadow
    drawLine(
        color = muted,
        start = Offset(mid, bookTop),
        end = Offset(mid, bookBottom),
        strokeWidth = w * 0.02f,
        cap = StrokeCap.Round,
    )

    // Left page
    val leftPath = Path().apply {
        moveTo(mid, bookTop)
        lineTo(left, bookTop + h * 0.06f)
        lineTo(left, bookBottom)
        lineTo(mid, bookBottom - h * 0.04f)
        close()
    }
    drawPath(leftPath, color = page)
    drawPath(leftPath, color = primary.copy(alpha = 0.35f), style = Stroke(width = w * 0.018f))

    // Right page
    val rightPath = Path().apply {
        moveTo(mid, bookTop)
        lineTo(right, bookTop + h * 0.06f)
        lineTo(right, bookBottom)
        lineTo(mid, bookBottom - h * 0.04f)
        close()
    }
    drawPath(rightPath, color = page)
    drawPath(rightPath, color = primary.copy(alpha = 0.35f), style = Stroke(width = w * 0.018f))

    // Check mark
    val check = Path().apply {
        moveTo(w * 0.42f, h * 0.5f)
        lineTo(w * 0.48f, h * 0.58f)
        lineTo(w * 0.62f, h * 0.4f)
    }
    drawPath(
        check,
        color = secondary,
        style = Stroke(width = w * 0.045f, cap = StrokeCap.Round),
    )
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawBookmarkBook(
    w: Float,
    h: Float,
    primary: Color,
    secondary: Color,
    muted: Color,
) {
    val left = w * 0.28f
    val top = h * 0.22f
    val bookW = w * 0.44f
    val bookH = h * 0.52f
    drawRoundRect(
        color = primary.copy(alpha = 0.9f),
        topLeft = Offset(left, top),
        size = Size(bookW, bookH),
        cornerRadius = CornerRadius(w * 0.04f),
    )
    drawRoundRect(
        color = muted,
        topLeft = Offset(left + bookW * 0.12f, top + bookH * 0.18f),
        size = Size(bookW * 0.76f, bookH * 0.08f),
        cornerRadius = CornerRadius(w * 0.02f),
    )
    drawRoundRect(
        color = muted,
        topLeft = Offset(left + bookW * 0.12f, top + bookH * 0.36f),
        size = Size(bookW * 0.55f, bookH * 0.08f),
        cornerRadius = CornerRadius(w * 0.02f),
    )

    // Bookmark ribbon
    val bx = left + bookW * 0.62f
    val ribbon = Path().apply {
        moveTo(bx, top - h * 0.04f)
        lineTo(bx + w * 0.1f, top - h * 0.04f)
        lineTo(bx + w * 0.1f, top + h * 0.28f)
        lineTo(bx + w * 0.05f, top + h * 0.22f)
        lineTo(bx, top + h * 0.28f)
        close()
    }
    drawPath(ribbon, color = secondary)
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawEmptyShelf(
    w: Float,
    h: Float,
    primary: Color,
    muted: Color,
    secondary: Color,
) {
    val shelfY = h * 0.68f
    drawRoundRect(
        color = primary.copy(alpha = 0.85f),
        topLeft = Offset(w * 0.12f, shelfY),
        size = Size(w * 0.76f, h * 0.06f),
        cornerRadius = CornerRadius(w * 0.02f),
    )
    // Faint upright placeholders
    listOf(0.22f, 0.38f, 0.54f, 0.7f).forEachIndexed { index, xFrac ->
        drawRoundRect(
            color = if (index == 1) secondary.copy(alpha = 0.25f) else muted.copy(alpha = 0.5f),
            topLeft = Offset(w * xFrac, h * 0.34f),
            size = Size(w * 0.1f, h * 0.32f),
            cornerRadius = CornerRadius(w * 0.02f),
        )
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawGrowthPath(
    w: Float,
    h: Float,
    primary: Color,
    secondary: Color,
    muted: Color,
) {
    // Path ascending like a learning curve
    val path = Path().apply {
        moveTo(w * 0.16f, h * 0.72f)
        quadraticBezierTo(w * 0.32f, h * 0.7f, w * 0.4f, h * 0.55f)
        quadraticBezierTo(w * 0.55f, h * 0.32f, w * 0.72f, h * 0.3f)
        quadraticBezierTo(w * 0.82f, h * 0.28f, w * 0.86f, h * 0.22f)
    }
    drawPath(
        path,
        color = primary,
        style = Stroke(width = w * 0.045f, cap = StrokeCap.Round),
    )
    drawCircle(color = secondary, radius = w * 0.045f, center = Offset(w * 0.86f, h * 0.22f))
    drawCircle(color = muted, radius = w * 0.03f, center = Offset(w * 0.16f, h * 0.72f))
    drawCircle(color = muted, radius = w * 0.03f, center = Offset(w * 0.4f, h * 0.55f))
}
