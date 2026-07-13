package com.msarangal.vocabmania.shared.domain.usecase

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.msarangal.vocabmania.shared.data.repository.SqlDelightWordRepository
import com.msarangal.vocabmania.shared.db.VocabManiaDatabase
import com.msarangal.vocabmania.shared.domain.model.DifficultyLevel
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FavoriteUseCasesTest {

    @Test
    fun toggleFavoriteSetsAndClearsFavorite() = runBlocking {
        val (toggle, getFavorites, insertWordId) = create()
        val wordId = insertWordId("ephemeral", DifficultyLevel.EASY)

        assertTrue(toggle(wordId) == true)
        assertEquals(1, getFavorites().size)
        assertEquals("ephemeral", getFavorites().first().text)

        assertTrue(toggle(wordId) == false)
        assertTrue(getFavorites().isEmpty())
    }

    @Test
    fun getFavoritesReturnsOnlyFavoritedWords() = runBlocking {
        val (toggle, getFavorites, insertWordId) = create()
        val favored = insertWordId("favored", DifficultyLevel.MEDIUM)
        insertWordId("not-favored", DifficultyLevel.MEDIUM)

        toggle(favored)

        val favorites = getFavorites()
        assertEquals(1, favorites.size)
        assertEquals("favored", favorites.first().text)
        assertTrue(favorites.first().isFavorite)
    }

    @Test
    fun toggleFavoriteReturnsNullForMissingWord() = runBlocking {
        val (toggle, _, _) = create()
        assertNull(toggle(999L))
    }

    private fun create(): Triple<ToggleFavoriteUseCase, GetFavoritesUseCase, suspend (String, DifficultyLevel) -> Long> {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        VocabManiaDatabase.Schema.create(driver)
        val database = VocabManiaDatabase(driver)
        val wordRepository = SqlDelightWordRepository(database)
        val toggle = ToggleFavoriteUseCase(wordRepository)
        val getFavorites = GetFavoritesUseCase(wordRepository)
        val insert: suspend (String, DifficultyLevel) -> Long = { text, level ->
            wordRepository.insertWord(
                text = text,
                meaning = "meaning",
                usageExample = null,
                level = level,
            )
        }
        return Triple(toggle, getFavorites, insert)
    }
}
