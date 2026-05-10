package com.example.android.dogsapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dogs")
data class DogEntity(
    @PrimaryKey val imageUrl: String,
)
