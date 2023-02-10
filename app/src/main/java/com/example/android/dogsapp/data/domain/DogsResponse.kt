package com.example.android.dogsapp.data.domain

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DogsResponse(
    val status: String,
    val message: List<String>,
)

