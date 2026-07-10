package com.msarangal.vocabmania.shared.domain.srs

/**
 * Helpers for upgrading Phase 1 review cards to SM-2 lite without resetting schedules.
 *
 * SQLDelight migration 2.sqm adds [ease_factor] with DEFAULT 2.5 — existing rows keep
 * [interval_days], [review_count], and [next_review_at] unchanged.
 */
object Sm2HistoryMigration {
    fun initialEaseFactorForExistingCard(): Double = Sm2LiteScheduler.DEFAULT_EASE_FACTOR
}
