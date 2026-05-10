package com.example.android.dogsapp.ui.favorites

import app.cash.turbine.test
import com.example.android.dogsapp.MainDispatcherRule
import com.example.android.dogsapp.data.domain.Dog
import com.example.android.dogsapp.fakes.FakeDogsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FavoritesViewModelTest {

    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    private val dogA = Dog("https://images.dog.ceo/breeds/akita/1.jpg")
    private val dogB = Dog("https://images.dog.ceo/breeds/beagle/2.jpg")

    @Test
    fun `favorites flow mirrors the repository`() = runTest {
        val repo = FakeDogsRepository(initialFavorites = listOf(dogA, dogB))
        val viewModel = FavoritesViewModel(repo)

        viewModel.favorites.test {
            val first = awaitItem()
            val list = if (first.isEmpty()) awaitItem() else first
            assertEquals(listOf(dogA, dogB), list)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `isEmpty is true when there are no favorites`() = runTest {
        val viewModel = FavoritesViewModel(FakeDogsRepository())

        viewModel.isEmpty.test {
            assertEquals(true, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `isEmpty is false when favorites exist`() = runTest {
        val viewModel = FavoritesViewModel(FakeDogsRepository(initialFavorites = listOf(dogA)))

        viewModel.isEmpty.test {
            val first = awaitItem()
            val value = if (first) awaitItem() else first
            assertEquals(false, value)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onDogClicked then onDogDetailNavigated round-trip the navigate state`() = runTest {
        val viewModel = FavoritesViewModel(FakeDogsRepository())

        viewModel.onDogClicked(dogA)
        assertEquals(dogA, viewModel.navigateToDetail.value)

        viewModel.onDogDetailNavigated()
        assertNull(viewModel.navigateToDetail.value)
    }
}
