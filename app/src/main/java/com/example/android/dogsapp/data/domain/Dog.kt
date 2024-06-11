package com.example.android.dogsapp.data.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Dog(
    val imageUrl: String,
): Parcelable