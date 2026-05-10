package com.example.android.dogsapp.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.android.dogsapp.data.domain.DogsResponse
import com.example.android.dogsapp.data.local.FavoriteEntity
import com.example.android.dogsapp.fakes.FakeDogDao
import com.example.android.dogsapp.fakes.FakeDogsApi
import com.example.android.dogsapp.fakes.FakeFavoriteDao
import com.example.android.dogsapp.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DogsRepositoryImplTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val url1 = "https://images.dog.ceo/breeds/akita/1.jpg"
    private val url2 = "https://images.dog.ceo/breeds/beagle/2.jpg"

    @Test
    fun `dogs maps DAO entities to domain models`() {
        val dogDao = FakeDogDao()
        val repo = DogsRepositoryImpl(FakeDogsApi(), dogDao, FakeFavoriteDao())

        runTest { dogDao.insertAll(listOf(com.example.android.dogsapp.data.local.DogEntity(url1))) }

        val dogs = repo.dogs.getOrAwaitValue()
        assertEquals(1, dogs.size)
        assertEquals(url1, dogs.first().imageUrl)
    }

    @Test
    fun `refresh replaces the cached dogs on success`() = runTest {
        val dogDao = FakeDogDao()
        val api = FakeDogsApi(DogsResponse(status = "success", message = listOf(url1, url2)))
        val repo = DogsRepositoryImpl(api, dogDao, FakeFavoriteDao())

        repo.refresh()

        val dogs = repo.dogs.getOrAwaitValue()
        assertEquals(listOf(url1, url2), dogs.map { it.imageUrl })
    }

    @Test
    fun `refresh throws when api status is not success`() = runTest {
        val api = FakeDogsApi(DogsResponse(status = "error", message = emptyList()))
        val repo = DogsRepositoryImpl(api, FakeDogDao(), FakeFavoriteDao())

        assertThrows(IllegalStateException::class.java) {
            kotlinx.coroutines.runBlocking { repo.refresh() }
        }
    }

    @Test
    fun `toggleFavorite adds when not favorited`() = runTest {
        val favoriteDao = FakeFavoriteDao()
        val repo = DogsRepositoryImpl(FakeDogsApi(), FakeDogDao(), favoriteDao)

        repo.toggleFavorite(url1)

        assertTrue(favoriteDao.isFavoriteOnce(url1))
    }

    @Test
    fun `toggleFavorite removes when already favorited`() = runTest {
        val favoriteDao = FakeFavoriteDao(listOf(FavoriteEntity(url1)))
        val repo = DogsRepositoryImpl(FakeDogsApi(), FakeDogDao(), favoriteDao)

        repo.toggleFavorite(url1)

        assertFalse(favoriteDao.isFavoriteOnce(url1))
    }

    @Test
    fun `favorites maps DAO entities to domain models`() {
        val favoriteDao = FakeFavoriteDao(listOf(FavoriteEntity(url1), FavoriteEntity(url2)))
        val repo = DogsRepositoryImpl(FakeDogsApi(), FakeDogDao(), favoriteDao)

        val favorites = repo.favorites.getOrAwaitValue()

        assertEquals(listOf(url1, url2), favorites.map { it.imageUrl })
    }
}
