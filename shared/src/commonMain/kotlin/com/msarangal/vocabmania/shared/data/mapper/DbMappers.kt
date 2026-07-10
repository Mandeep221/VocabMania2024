package com.msarangal.vocabmania.shared.data.mapper

import com.msarangal.vocabmania.shared.db.Review_card
import com.msarangal.vocabmania.shared.db.SelectDue
import com.msarangal.vocabmania.shared.db.User_settings
import com.msarangal.vocabmania.shared.db.Word as WordEntity
import com.msarangal.vocabmania.shared.domain.model.DifficultyLevel
import com.msarangal.vocabmania.shared.domain.model.DueWord
import com.msarangal.vocabmania.shared.domain.model.ReviewCard
import com.msarangal.vocabmania.shared.domain.model.UserSettings
import com.msarangal.vocabmania.shared.domain.model.Word

fun WordEntity.toDomain(): Word = Word(
    id = id,
    text = text,
    meaning = meaning,
    usageExample = usage_example,
    level = DifficultyLevel.fromCode(level),
    isFavorite = is_favorite == 1L,
)

fun Review_card.toDomain(): ReviewCard = ReviewCard(
    wordId = word_id,
    nextReviewAtEpochMillis = next_review_at,
    intervalDays = interval_days,
    lastReviewedAtEpochMillis = last_reviewed_at,
    reviewCount = review_count.toInt(),
    easeFactor = ease_factor,
)

fun SelectDue.toDueWord(): DueWord = DueWord(
    word = Word(
        id = id,
        text = text,
        meaning = meaning,
        usageExample = usage_example,
        level = DifficultyLevel.fromCode(level),
        isFavorite = is_favorite == 1L,
    ),
    reviewCard = ReviewCard(
        wordId = word_id,
        nextReviewAtEpochMillis = next_review_at,
        intervalDays = interval_days,
        lastReviewedAtEpochMillis = last_reviewed_at,
        reviewCount = review_count.toInt(),
        easeFactor = ease_factor,
    ),
)

fun User_settings.toDomain(): UserSettings = UserSettings(
    onboardingComplete = onboarding_complete == 1L,
    selectedLevel = DifficultyLevel.fromCode(selected_level),
    dailyGoal = daily_goal.toInt(),
    currentStreak = current_streak.toInt(),
    longestStreak = longest_streak.toInt(),
    lastSessionEpochDay = last_session_epoch_day,
)
