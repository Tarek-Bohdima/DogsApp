package com.example.android.dogsapp.ui.details

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.example.android.dogsapp.MainDispatcherRule
import com.example.android.dogsapp.data.domain.Dog
import com.example.android.dogsapp.fakes.FakeDogsRepository
import com.example.android.dogsapp.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DetailsViewModelTest {

    @get:Rule val instantTaskExecutorRule = InstantTaskExecutorRule()
    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    private val dog = Dog("https://images.dog.ceo/breeds/akita/1.jpg")
    private val savedState = SavedStateHandle(mapOf("dog" to dog))

    @Test
    fun `dog is read from SavedStateHandle`() {
        val viewModel = DetailsViewModel(FakeDogsRepository(), savedState)
        assertEquals(dog, viewModel.dog)
    }

    @Test
    fun `isFavorite reflects the repository state`() {
        val repo = FakeDogsRepository(initialFavorites = listOf(dog))
        val viewModel = DetailsViewModel(repo, savedState)

        assertEquals(true, viewModel.isFavorite.getOrAwaitValue())
    }

    @Test
    fun `toggleFavorite delegates to the repository for the current dog`() {
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
