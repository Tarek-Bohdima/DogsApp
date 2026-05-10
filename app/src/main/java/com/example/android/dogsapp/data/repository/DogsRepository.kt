package com.example.android.dogsapp.data.repository

import com.example.android.dogsapp.data.domain.Dog
import kotlinx.coroutines.flow.Flow

interface DogsRepository {
    val dogs: Flow<List<Dog>>
    val favorites: Flow<List<Dog>>
    fun isFavorite(imageUrl: String): Flow<Boolean>
    suspend fun toggleFavorite(imageUrl: String)
    suspend fun refresh()
}
