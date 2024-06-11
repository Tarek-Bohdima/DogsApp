package com.example.android.dogsapp.data.network

import com.example.android.dogsapp.data.domain.DogsResponse
import retrofit2.http.GET

interface DogsApi {
    @GET("breeds/image/random/50")
    suspend fun getRandomDogs(): DogsResponse
}
