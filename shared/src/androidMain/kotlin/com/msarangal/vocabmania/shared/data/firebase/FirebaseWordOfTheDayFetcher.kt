package com.msarangal.vocabmania.shared.data.firebase

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

private const val FIREBASE_DATABASE_URL = "https://boiling-torch-469.firebaseio.com"
private const val RANDOMIZE_PATH = "/vocabmania/randomize"

data class FetchedWordOfTheDay(
    val word: String,
    val meaning: String,
    val usageExample: String?,
)

class FirebaseWordOfTheDayFetcher {
    suspend fun fetch(): FetchedWordOfTheDay? {
        val snapshot = fetchRandomizeSnapshot()
        return mapSnapshot(snapshot)
    }

    private suspend fun fetchRandomizeSnapshot(): DataSnapshot {
        val database = FirebaseDatabase.getInstance(FIREBASE_DATABASE_URL)
        val reference = database.getReference(RANDOMIZE_PATH)
        return reference.get().await()
    }

    internal fun mapSnapshot(snapshot: DataSnapshot): FetchedWordOfTheDay? {
        for (child in snapshot.children) {
            val word = child.child("word").getValue(String::class.java)?.trim().orEmpty()
            val meaning = child.child("meaning").getValue(String::class.java)?.trim().orEmpty()
            if (word.isEmpty() || meaning.isEmpty()) continue
            val usage = child.child("usage").getValue(String::class.java)?.trim()
            return FetchedWordOfTheDay(
                word = word,
                meaning = meaning,
                usageExample = usage?.takeIf { it.isNotEmpty() },
            )
        }
        return null
    }

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
