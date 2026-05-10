package com.example.android.dogsapp.ui.main

import app.cash.turbine.test
import com.example.android.dogsapp.MainDispatcherRule
import com.example.android.dogsapp.data.domain.Dog
import com.example.android.dogsapp.fakes.FakeDogsRepository
import com.example.android.dogsapp.fakes.FakeRefreshManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    private val sampleDog = Dog("https://images.dog.ceo/breeds/akita/1.jpg")

    @Test
    fun `init triggers refresh and sets status to DONE on success`() = runTest {
        val repo = FakeDogsRepository()
        val viewModel = MainViewModel(repo, FakeRefreshManager())

        assertEquals(1, repo.refreshCalls)
        assertEquals(DogsApiStatus.DONE, viewModel.status.value)
    }

    @Test
    fun `status flips to ERROR when refresh throws`() = runTest {
        val repo = FakeDogsRepository().apply { refreshError = RuntimeException("boom") }
        val viewModel = MainViewModel(repo, FakeRefreshManager())

        assertEquals(DogsApiStatus.ERROR, viewModel.status.value)
    }

    @Test
    fun `refreshDogs triggers another refresh and tells refresh manager to stop`() = runTest {
        val repo = FakeDogsRepository()
        val refreshManager = FakeRefreshManager().apply { setRefreshing(true) }
        val viewModel = MainViewModel(repo, refreshManager)

        viewModel.refreshDogs()

        assertEquals(2, repo.refreshCalls)
        assertEquals(false, refreshManager.isRefreshing())
    }

    @Test
    fun `dogs flow mirrors the repository`() = runTest {
        val repo = FakeDogsRepository(initialDogs = listOf(sampleDog))
        val viewModel = MainViewModel(repo, FakeRefreshManager())

        viewModel.dogs.test {
            val first = awaitItem()
            val list = if (first.isEmpty()) awaitItem() else first
            assertEquals(listOf(sampleDog), list)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onDogClicked then onDogDetailNavigated round-trip the navigate state`() = runTest {
        val viewModel = MainViewModel(FakeDogsRepository(), FakeRefreshManager())

        viewModel.onDogClicked(sampleDog)
        assertEquals(sampleDog, viewModel.navigateToDetail.value)

        viewModel.onDogDetailNavigated()
        assertNull(viewModel.navigateToDetail.value)
    }
}
