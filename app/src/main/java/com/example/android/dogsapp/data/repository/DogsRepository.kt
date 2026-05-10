package com.example.android.dogsapp.data.repository

import androidx.lifecycle.LiveData
import com.example.android.dogsapp.data.domain.Dog

interface DogsRepository {
    val dogs: LiveData<List<Dog>>
    suspend fun refresh()
}
