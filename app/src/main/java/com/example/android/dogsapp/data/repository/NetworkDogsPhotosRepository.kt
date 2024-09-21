package com.example.android.dogsapp.data.repository

import com.example.android.dogsapp.data.domain.DogsResponse
import com.example.android.dogsapp.data.network.DogsApi

interface DogsRepository {
    suspend fun getDogsPhotos(): DogsResponse
}

class NetworkDogsPhotosRepository(private val dogsApi: DogsApi): DogsRepository {

    override suspend fun getDogsPhotos(): DogsResponse = dogsApi.getRandomDogs()
}
