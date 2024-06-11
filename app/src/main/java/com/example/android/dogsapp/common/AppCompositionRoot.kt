package com.example.android.dogsapp.common

import com.example.android.dogsapp.data.network.DogsApi
import com.example.android.dogsapp.data.repository.DogsRepository
import com.example.android.dogsapp.data.repository.NetworkDogsPhotosRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class AppCompositionRoot {
    // https://dog.ceo/api/breeds/image/random/50
private val baseUrl = "https://dog.ceo/api/"

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    val dogsApi: DogsApi by lazy { retrofit.create(DogsApi::class.java) }

    val dogsPhotoRepository: DogsRepository by lazy {
        NetworkDogsPhotosRepository(dogsApi)
    }
}
