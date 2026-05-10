package com.example.android.dogsapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [DogEntity::class], version = 1, exportSchema = false)
abstract class DogsDatabase : RoomDatabase() {
    abstract fun dogDao(): DogDao
}
