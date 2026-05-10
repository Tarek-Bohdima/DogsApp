package com.example.android.dogsapp.data.repository

import androidx.lifecycle.LiveData
import com.example.android.dogsapp.data.domain.Dog

interface DogsRepository {
    val dogs: LiveData<List<Dog>>
    val favorites: LiveData<List<Dog>>
    fun isFavorite(imageUrl: String): LiveData<Boolean>
    suspend fun toggleFavorite(imageUrl: String)
    suspend fun refresh()
}
