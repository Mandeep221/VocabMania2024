package com.msarangal.vocabmania.shared.domain.usecase

import com.msarangal.vocabmania.shared.domain.model.DueWord
import com.msarangal.vocabmania.shared.domain.model.PracticeCard
import kotlin.math.ceil

/**
 * Home / session bite size: never promise more words than are due or than the daily goal.
 */
fun practiceSessionBiteSize(dueCount: Int, dailyGoal: Int): Int =
    minOf(dueCount.coerceAtLeast(0), dailyGoal.coerceAtLeast(0))

/** Soft backlog line on Home only when due exceeds the daily goal. */
fun practiceShowsQueueLine(dueCount: Int, dailyGoal: Int): Boolean =
    dueCount > dailyGoal

/** Max first-seen cards in a bite: min(5, ceil(N / 2)). */
fun practiceNewCap(sessionSize: Int): Int {
    val n = sessionSize.coerceAtLeast(0)
    if (n == 0) return 0
    return minOf(5, ceil(n / 2.0).toInt())
}

/**
 * Order a practice bite: new cards first (up to new-cap), then repeats, truncated to [sessionSize].
 * Preserves relative due order within each group. May return fewer than [sessionSize] if not enough candidates.
 */
fun orderPracticeSession(candidates: List<DueWord>, sessionSize: Int): List<PracticeCard> {
    val n = sessionSize.coerceAtLeast(0)
    if (n == 0 || candidates.isEmpty()) return emptyList()

    val newCap = practiceNewCap(n)
    val newCards = candidates.filter { it.reviewCard.reviewCount == 0 }.take(newCap)
    val repeatCards = candidates.filter { it.reviewCard.reviewCount > 0 }

    return (newCards + repeatCards)
        .take(n)
        .map { due ->
            PracticeCard(
                dueWord = due,
                isNew = due.reviewCard.reviewCount == 0,
            )
        }
}
