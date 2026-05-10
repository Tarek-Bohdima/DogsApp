package com.example.android.dogsapp.ui.details

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.example.android.dogsapp.MainDispatcherRule
import com.example.android.dogsapp.data.domain.Dog
import com.example.android.dogsapp.fakes.FakeDogsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DetailsViewModelTest {

    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    private val dog = Dog("https://images.dog.ceo/breeds/akita/1.jpg")
    private val savedState = SavedStateHandle(mapOf("dog" to dog))

    @Test
    fun `dog is read from SavedStateHandle`() {
        val viewModel = DetailsViewModel(FakeDogsRepository(), savedState)
        assertEquals(dog, viewModel.dog)
    }

    @Test
    fun `isFavorite reflects the repository state`() = runTest {
        val repo = FakeDogsRepository(initialFavorites = listOf(dog))
        val viewModel = DetailsViewModel(repo, savedState)

        viewModel.isFavorite.test {
            val first = awaitItem()
            val isFav = if (!first) awaitItem() else first
            assertEquals(true, isFav)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `toggleFavorite delegates to the repository for the current dog`() = runTest {
        val repo = FakeDogsRepository()
        val viewModel = DetailsViewModel(repo, savedState)

        viewModel.toggleFavorite()

        assertEquals(1, repo.toggleCalls)
        assertEquals(listOf(dog.imageUrl), repo.toggledUrls)
    }

    @Test
    fun `missing dog nav argument blows up the ViewModel`() {
        val emptyState = SavedStateHandle()
        val ex = assertThrows(IllegalStateException::class.java) {
            DetailsViewModel(FakeDogsRepository(), emptyState)
        }
        assertTrue(ex.message?.contains("dog") == true)
    }
}
