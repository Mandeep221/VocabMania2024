package com.msarangal.vocabmania.shared.data.firebase

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.msarangal.vocabmania.shared.data.catalog.FirebaseWordRecordMapper
import com.msarangal.vocabmania.shared.domain.repository.ReviewRepository
import com.msarangal.vocabmania.shared.domain.repository.WordRepository
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

private const val FIREBASE_DATABASE_URL = "https://boiling-torch-469.firebaseio.com"
private const val QUESTIONS_PATH = "/vocabmania/questions"

class FirebaseWordCatalogImporter(
    private val wordRepository: WordRepository,
    private val reviewRepository: ReviewRepository,
) {
    suspend fun importAll(nowEpochMillis: Long): ImportStats {
        val snapshot = fetchQuestionsSnapshot()
        var imported = 0
        var skipped = 0

        for (child in snapshot.children) {
            val mapped = mapSnapshot(child)
            if (mapped == null) {
                skipped++
                continue
            }

            val wordId = wordRepository.insertWord(
                text = mapped.text,
                meaning = mapped.meaning,
                usageExample = mapped.usageExample,
                level = mapped.level,
                firebaseLevelAttempt = mapped.firebaseLevelAttempt,
            )
            reviewRepository.ensureReviewCard(wordId, nowEpochMillis)
            imported++
        }

        return ImportStats(imported = imported, skipped = skipped)
    }

    private suspend fun fetchQuestionsSnapshot(): DataSnapshot {
        val database = FirebaseDatabase.getInstance(FIREBASE_DATABASE_URL)
        val reference = database.getReference(QUESTIONS_PATH)
        return reference.get().await()
    }

    private fun mapSnapshot(snapshot: DataSnapshot) =
        FirebaseWordRecordMapper.map(
            word = snapshot.child("word").getValue(String::class.java),
            question = snapshot.child("question").getValue(String::class.java),
            op1 = snapshot.child("op1").getValue(String::class.java),
            op2 = snapshot.child("op2").getValue(String::class.java),
            op3 = snapshot.child("op3").getValue(String::class.java),
            answerRaw = snapshot.child("answer").getValue(String::class.java),
            levelAttempt = snapshot.child("level_attempt").getValue(String::class.java),
        )

    private suspend fun <T> Task<T>.await(): T = suspendCoroutine { continuation ->
        addOnCompleteListener { task ->
            if (task.isSuccessful) {
                continuation.resume(task.result)
            } else {
                continuation.resumeWithException(
                    task.exception ?: IllegalStateException("Firebase task failed without an exception."),
                )
            }
        }
    }
}

data class ImportStats(
    val imported: Int,
    val skipped: Int,
)
