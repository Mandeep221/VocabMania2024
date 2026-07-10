package com.msarangal.vocabmania.shared.data.catalog

import com.msarangal.vocabmania.shared.domain.model.DifficultyLevel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class FirebaseWordRecordMapperTest {
    @Test
    fun mapsValidFirebaseRecord() {
        val mapped = FirebaseWordRecordMapper.map(
            word = "ephemeral",
            question = "The beauty of cherry blossoms is ___.",
            op1 = "lasting",
            op2 = "fleeting",
            op3 = "colorful",
            answerRaw = "2",
            levelAttempt = "E_1",
        )

        assertNotNull(mapped)
        assertEquals("ephemeral", mapped.text)
        assertEquals("fleeting", mapped.meaning)
        assertEquals("The beauty of cherry blossoms is ___.", mapped.usageExample)
        assertEquals(DifficultyLevel.EASY, mapped.level)
        assertEquals("E_1", mapped.firebaseLevelAttempt)
    }

    @Test
    fun skipsMalformedRecords() {
        assertNull(
            FirebaseWordRecordMapper.map(
                word = "",
                question = "Example",
                op1 = "a",
                op2 = "b",
                op3 = "c",
                answerRaw = "1",
                levelAttempt = "M_2",
            ),
        )
        assertNull(
            FirebaseWordRecordMapper.map(
                word = "valid",
                question = "Example",
                op1 = "a",
                op2 = "b",
                op3 = "c",
                answerRaw = "9",
                levelAttempt = "M_2",
            ),
        )
        assertNull(
            FirebaseWordRecordMapper.map(
                word = "valid",
                question = "Example",
                op1 = "a",
                op2 = "b",
                op3 = "c",
                answerRaw = "1",
                levelAttempt = null,
            ),
        )
    }
}
