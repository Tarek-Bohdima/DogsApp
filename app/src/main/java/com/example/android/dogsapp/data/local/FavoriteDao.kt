package com.example.android.dogsapp.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FavoriteDao {

    @Query("SELECT * FROM favorites ORDER BY imageUrl")
    fun observeFavorites(): LiveData<List<FavoriteEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE imageUrl = :imageUrl)")
    fun isFavorite(imageUrl: String): LiveData<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE imageUrl = :imageUrl)")
    suspend fun isFavoriteOnce(imageUrl: String): Boolean

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun add(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE imageUrl = :imageUrl")
    suspend fun remove(imageUrl: String)
}
