package com.msarangal.vocabmania.shared.domain.usecase

import com.msarangal.vocabmania.shared.domain.model.DifficultyLevel
import com.msarangal.vocabmania.shared.domain.model.DueWord
import com.msarangal.vocabmania.shared.domain.model.ReviewCard
import com.msarangal.vocabmania.shared.domain.model.Word
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PracticeSessionSizingTest {

    @Test
    fun biteSizeCapsAtGoalWhenBacklogExceedsGoal() {
        assertEquals(15, practiceSessionBiteSize(dueCount = 669, dailyGoal = 15))
    }

    @Test
    fun biteSizeUsesDueWhenFewerThanGoal() {
        assertEquals(7, practiceSessionBiteSize(dueCount = 7, dailyGoal = 15))
    }

    @Test
    fun biteSizeZeroWhenCaughtUp() {
        assertEquals(0, practiceSessionBiteSize(dueCount = 0, dailyGoal = 15))
    }

    @Test
    fun queueLineOnlyWhenDueExceedsGoal() {
        assertTrue(practiceShowsQueueLine(dueCount = 20, dailyGoal = 15))
        assertFalse(practiceShowsQueueLine(dueCount = 15, dailyGoal = 15))
        assertFalse(practiceShowsQueueLine(dueCount = 7, dailyGoal = 15))
        assertFalse(practiceShowsQueueLine(dueCount = 0, dailyGoal = 15))
    }

    @Test
    fun newCapIsMinFiveAndCeilHalfN() {
        assertEquals(0, practiceNewCap(0))
        assertEquals(1, practiceNewCap(1))
        assertEquals(1, practiceNewCap(2))
        assertEquals(4, practiceNewCap(7))
        assertEquals(5, practiceNewCap(10))
        assertEquals(5, practiceNewCap(20))
    }

    @Test
    fun orderPutsNewFirstWithinNewCapThenRepeatsTruncatedToN() {
        val candidates = listOf(
            due(id = 1, text = "new-a", reviewCount = 0),
            due(id = 2, text = "new-b", reviewCount = 0),
            due(id = 3, text = "new-c", reviewCount = 0),
            due(id = 4, text = "rep-a", reviewCount = 2),
            due(id = 5, text = "rep-b", reviewCount = 1),
            due(id = 6, text = "rep-c", reviewCount = 3),
        )
        // N=4 → newCap=2 → new-a, new-b, then rep-a, rep-b
        val session = orderPracticeSession(candidates, sessionSize = 4)
        assertEquals(listOf("new-a", "new-b", "rep-a", "rep-b"), session.map { it.word.text })
        assertTrue(session[0].isNew && session[1].isNew)
        assertFalse(session[2].isNew || session[3].isNew)
    }

    @Test
    fun orderHonestLengthWhenNotEnoughRepeatsAfterNewCap() {
        val candidates = (1..8).map { i ->
            due(id = i.toLong(), text = "new-$i", reviewCount = 0)
        }
        // N=6 → newCap=3 → only 3 cards when no repeats
        val session = orderPracticeSession(candidates, sessionSize = 6)
        assertEquals(3, session.size)
        assertTrue(session.all { it.isNew })
    }

    @Test
    fun orderAllRepeatsWhenNoNewCards() {
        val candidates = listOf(
            due(id = 1, text = "rep-a", reviewCount = 1),
            due(id = 2, text = "rep-b", reviewCount = 2),
            due(id = 3, text = "rep-c", reviewCount = 3),
        )
        val session = orderPracticeSession(candidates, sessionSize = 2)
        assertEquals(listOf("rep-a", "rep-b"), session.map { it.word.text })
        assertTrue(session.none { it.isNew })
    }

    private fun due(id: Long, text: String, reviewCount: Int): DueWord =
        DueWord(
            word = Word(
                id = id,
                text = text,
                meaning = "m",
                usageExample = null,
                level = DifficultyLevel.EASY,
                isFavorite = false,
            ),
            reviewCard = ReviewCard(
                wordId = id,
                nextReviewAtEpochMillis = 1_000L + id,
                intervalDays = if (reviewCount == 0) 0.0 else 1.0,
                lastReviewedAtEpochMillis = if (reviewCount == 0) null else 500L,
                reviewCount = reviewCount,
                easeFactor = 2.5,
            ),
        )
}
