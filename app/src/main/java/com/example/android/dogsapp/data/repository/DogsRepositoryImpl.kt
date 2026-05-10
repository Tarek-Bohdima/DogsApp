package com.example.android.dogsapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.android.dogsapp.data.domain.Dog
import com.example.android.dogsapp.data.local.DogDao
import com.example.android.dogsapp.data.local.DogEntity
import com.example.android.dogsapp.data.network.DogsApi
import javax.inject.Inject

class DogsRepositoryImpl @Inject constructor(
    private val dogsApi: DogsApi,
    private val dogDao: DogDao,
) : DogsRepository {

    override val dogs: LiveData<List<Dog>> = dogDao.observeDogs().map { entities ->
        entities.map { Dog(it.imageUrl) }
    }

    override suspend fun refresh() {
        val response = dogsApi.getRandomDogs()
        if (response.status != "success") {
            error("Unexpected API status: ${response.status}")
        }
        dogDao.replaceAll(response.message.map { DogEntity(it) })
    }
}
