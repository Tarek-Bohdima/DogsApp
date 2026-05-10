package com.example.android.dogsapp.data.repository

import app.cash.turbine.test
import com.example.android.dogsapp.data.domain.DogsResponse
import com.example.android.dogsapp.data.local.DogEntity
import com.example.android.dogsapp.data.local.FavoriteEntity
import com.example.android.dogsapp.fakes.FakeDogDao
import com.example.android.dogsapp.fakes.FakeDogsApi
import com.example.android.dogsapp.fakes.FakeFavoriteDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DogsRepositoryImplTest {

    private val url1 = "https://images.dog.ceo/breeds/akita/1.jpg"
    private val url2 = "https://images.dog.ceo/breeds/beagle/2.jpg"

    @Test
    fun `dogs flow maps DAO entities to domain models`() = runTest {
        val dogDao = FakeDogDao(initial = listOf(DogEntity(url1)))
        val repo: DogsRepository = DogsRepositoryImpl(FakeDogsApi(), dogDao, FakeFavoriteDao())

        val dogs = repo.dogs.first()
        assertEquals(1, dogs.size)
        assertEquals(url1, dogs.first().imageUrl)
    }

    @Test
    fun `refresh replaces the cached dogs on success`() = runTest {
        val dogDao = FakeDogDao()
        val api = FakeDogsApi(DogsResponse(status = "success", message = listOf(url1, url2)))
        val repo: DogsRepository = DogsRepositoryImpl(api, dogDao, FakeFavoriteDao())

        repo.refresh()

        val dogs = repo.dogs.first()
        assertEquals(listOf(url1, url2), dogs.map { it.imageUrl })
    }

    @Test
    fun `refresh throws when api status is not success`() {
        val api = FakeDogsApi(DogsResponse(status = "error", message = emptyList()))
        val repo: DogsRepository = DogsRepositoryImpl(api, FakeDogDao(), FakeFavoriteDao())

        assertThrows(IllegalStateException::class.java) {
            kotlinx.coroutines.runBlocking { repo.refresh() }
        }
    }

    @Test
    fun `toggleFavorite adds when not favorited`() = runTest {
        val favoriteDao = FakeFavoriteDao()
        val repo: DogsRepository = DogsRepositoryImpl(FakeDogsApi(), FakeDogDao(), favoriteDao)

        repo.toggleFavorite(url1)

        assertTrue(favoriteDao.isFavoriteOnce(url1))
    }

    @Test
    fun `toggleFavorite removes when already favorited`() = runTest {
        val favoriteDao = FakeFavoriteDao(initial = listOf(FavoriteEntity(url1)))
        val repo: DogsRepository = DogsRepositoryImpl(FakeDogsApi(), FakeDogDao(), favoriteDao)

        repo.toggleFavorite(url1)

        assertFalse(favoriteDao.isFavoriteOnce(url1))
    }

    @Test
    fun `favorites flow maps DAO entities to domain models`() = runTest {
        val favoriteDao = FakeFavoriteDao(initial = listOf(FavoriteEntity(url1), FavoriteEntity(url2)))
        val repo: DogsRepository = DogsRepositoryImpl(FakeDogsApi(), FakeDogDao(), favoriteDao)

        val favorites = repo.favorites.first()
        assertEquals(listOf(url1, url2), favorites.map { it.imageUrl })
    }

    @Test
    fun `isFavorite emits updated value when toggled`() = runTest {
        val repo: DogsRepository = DogsRepositoryImpl(FakeDogsApi(), FakeDogDao(), FakeFavoriteDao())

        repo.isFavorite(url1).test {
            assertEquals(false, awaitItem())
            repo.toggleFavorite(url1)
            assertEquals(true, awaitItem())
        }
    }
}
