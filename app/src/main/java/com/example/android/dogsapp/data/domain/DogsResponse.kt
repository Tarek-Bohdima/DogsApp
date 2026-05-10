package com.example.android.dogsapp.data.domain

import kotlinx.serialization.Serializable

@Serializable
data class DogsResponse(
    val status: String,
    val message: List<String>,
)
