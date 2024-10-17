package com.example.android.dogsapp.data.repository

import com.example.android.dogsapp.common.di.application.ApplicationScope
import com.example.android.dogsapp.data.domain.DogsResponse
import com.example.android.dogsapp.data.network.DogsApi
import javax.inject.Inject

@ApplicationScope
class DogsRepositoryImpl @Inject constructor(private val dogsApi: DogsApi): DogsRepository {

    override suspend fun getDogsPhotos(): DogsResponse = dogsApi.getRandomDogs()
}
