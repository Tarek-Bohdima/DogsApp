package com.example.android.dogsapp.data.repository

import com.example.android.dogsapp.data.domain.Dog
import com.example.android.dogsapp.data.local.DogDao
import com.example.android.dogsapp.data.local.DogEntity
import com.example.android.dogsapp.data.local.FavoriteDao
import com.example.android.dogsapp.data.local.FavoriteEntity
import com.example.android.dogsapp.data.network.DogsApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DogsRepositoryImpl @Inject constructor(
    private val dogsApi: DogsApi,
    private val dogDao: DogDao,
    private val favoriteDao: FavoriteDao,
) : DogsRepository {

    override val dogs: Flow<List<Dog>> = dogDao.observeDogs().map { entities ->
        entities.map { Dog(it.imageUrl) }
    }

    override val favorites: Flow<List<Dog>> = favoriteDao.observeFavorites().map { entities ->
        entities.map { Dog(it.imageUrl) }
    }

    override fun isFavorite(imageUrl: String): Flow<Boolean> =
        favoriteDao.isFavorite(imageUrl)

    override suspend fun toggleFavorite(imageUrl: String) {
        if (favoriteDao.isFavoriteOnce(imageUrl)) {
            favoriteDao.remove(imageUrl)
        } else {
            favoriteDao.add(FavoriteEntity(imageUrl))
        }
    }

    override suspend fun refresh() {
        val response = dogsApi.getRandomDogs()
        if (response.status != "success") {
            error("Unexpected API status: ${response.status}")
        }
        dogDao.replaceAll(response.message.map { DogEntity(it) })
    }
}
