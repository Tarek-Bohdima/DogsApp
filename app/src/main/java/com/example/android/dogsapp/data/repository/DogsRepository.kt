package com.example.android.dogsapp.data.repository

import com.example.android.dogsapp.data.domain.DogsResponse

interface DogsRepository {
    suspend fun getDogsPhotos(): DogsResponse
}
