package com.example.android.dogsapp.data.network

import com.example.android.dogsapp.data.domain.DogsResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

// https://dog.ceo/api/breeds/image/random/50
private const val BASE_URL = "https://dog.ceo/api/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface DogsApiService {
    @GET("breeds/image/random/50")
    suspend fun getRandomDogs(): DogsResponse
}

object DogsApi {
    val retrofitService: DogsApiService by lazy { retrofit.create(DogsApiService::class.java) }
}