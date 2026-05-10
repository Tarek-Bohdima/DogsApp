package com.example.android.dogsapp.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.android.dogsapp.MainDispatcherRule
import com.example.android.dogsapp.data.domain.Dog
import com.example.android.dogsapp.fakes.FakeDogsRepository
import com.example.android.dogsapp.fakes.FakeRefreshManager
import com.example.android.dogsapp.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    @get:Rule val instantTaskExecutorRule = InstantTaskExecutorRule()
    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    private val sampleDog = Dog("https://images.dog.ceo/breeds/akita/1.jpg")

    @Test
    fun `init triggers refresh and sets status to DONE on success`() {
        val repo = FakeDogsRepository()
        val viewModel = MainViewModel(repo, FakeRefreshManager())

        assertEquals(1, repo.refreshCalls)
        assertEquals(DogsApiStatus.DONE, viewModel.status.getOrAwaitValue())
    }

    @Test
    fun `status flips to ERROR when refresh throws`() {
        val repo = FakeDogsRepository().apply { refreshError = RuntimeException("boom") }
        val viewModel = MainViewModel(repo, FakeRefreshManager())

        assertEquals(DogsApiStatus.ERROR, viewModel.status.getOrAwaitValue())
    }

    @Test
    fun `refreshDogs triggers another refresh and tells refresh manager to stop`() {
        val repo = FakeDogsRepository()
        val refreshManager = FakeRefreshManager().apply { setRefreshing(true) }
        val viewModel = MainViewModel(repo, refreshManager)

        viewModel.refreshDogs()

        assertEquals(2, repo.refreshCalls)
        assertEquals(false, refreshManager.isRefreshing())
    }

    @Test
    fun `dogs LiveData mirrors the repository`() {
        val repo = FakeDogsRepository(initialDogs = listOf(sampleDog))
        val viewModel = MainViewModel(repo, FakeRefreshManager())

        assertEquals(listOf(sampleDog), viewModel.dogs.getOrAwaitValue())
    }

    @Test
    fun `onDogClicked then onDogDetailNavigated round-trip the navigate state`() {
        val viewModel = MainViewModel(FakeDogsRepository(), FakeRefreshManager())

        viewModel.onDogClicked(sampleDog)
        assertEquals(sampleDog, viewModel.navigateToDetail.getOrAwaitValue())

        viewModel.onDogDetailNavigated()
        assertNull(viewModel.navigateToDetail.getOrAwaitValue())
    }
}
