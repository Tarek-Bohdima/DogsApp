package com.example.android.dogsapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface DogDao {

    @Query("SELECT * FROM dogs")
    fun observeDogs(): Flow<List<DogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(dogs: List<DogEntity>)

    @Query("DELETE FROM dogs")
    suspend fun clearAll()

    @Transaction
    suspend fun replaceAll(dogs: List<DogEntity>) {
        clearAll()
        insertAll(dogs)
    }
}
