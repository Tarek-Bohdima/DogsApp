package com.example.android.dogsapp.data.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Dog(
    val imageUrl: String,
) : Parcelable

fun Dog.displayBreedName(): String =
    imageUrl.split("/").getOrNull(4)
        ?.replace("-", " ")
        ?.split(" ")
        ?.joinToString(" ") { it.replaceFirstChar(Char::uppercase) }
        ?: ""
