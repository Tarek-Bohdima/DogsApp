package com.example.android.dogsapp.ui.favorites

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.android.dogsapp.data.domain.Dog
import com.example.android.dogsapp.fakes.FakeDogsRepository
import com.example.android.dogsapp.getOrAwaitValue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

class FavoritesViewModelTest {

    @get:Rule val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val dogA = Dog("https://images.dog.ceo/breeds/akita/1.jpg")
    private val dogB = Dog("https://images.dog.ceo/breeds/beagle/2.jpg")

    @Test
    fun `favorites mirrors the repository`() {
        val repo = FakeDogsRepository(initialFavorites = listOf(dogA, dogB))
        val viewModel = FavoritesViewModel(repo)

        assertEquals(listOf(dogA, dogB), viewModel.favorites.getOrAwaitValue())
    }

    @Test
    fun `isEmpty is true when there are no favorites`() {
        val viewModel = FavoritesViewModel(FakeDogsRepository())

        assertEquals(true, viewModel.isEmpty.getOrAwaitValue())
    }

    @Test
    fun `isEmpty is false when favorites exist`() {
        val viewModel = FavoritesViewModel(FakeDogsRepository(initialFavorites = listOf(dogA)))

        assertEquals(false, viewModel.isEmpty.getOrAwaitValue())
    }

    @Test
    fun `onDogClicked then onDogDetailNavigated round-trip the navigate state`() {
        val viewModel = FavoritesViewModel(FakeDogsRepository())

        viewModel.onDogClicked(dogA)
        assertEquals(dogA, viewModel.navigateToDetail.getOrAwaitValue())

        viewModel.onDogDetailNavigated()
        assertNull(viewModel.navigateToDetail.getOrAwaitValue())
    }
}
