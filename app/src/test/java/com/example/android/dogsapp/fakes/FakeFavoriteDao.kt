package com.example.android.dogsapp.fakes

import com.example.android.dogsapp.data.local.FavoriteDao
import com.example.android.dogsapp.data.local.FavoriteEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class FakeFavoriteDao(initial: List<FavoriteEntity> = emptyList()) : FavoriteDao {
    private val state = MutableStateFlow(initial)

    override fun observeFavorites(): Flow<List<FavoriteEntity>> = state.asStateFlow()

    override fun isFavorite(imageUrl: String): Flow<Boolean> =
        state.map { list -> list.any { it.imageUrl == imageUrl } }

    override suspend fun isFavoriteOnce(imageUrl: String): Boolean =
        state.value.any { it.imageUrl == imageUrl }

    override suspend fun add(favorite: FavoriteEntity) {
        val current = state.value
        if (current.none { it.imageUrl == favorite.imageUrl }) {
            state.value = current + favorite
        }
    }

    override suspend fun remove(imageUrl: String) {
        state.value = state.value.filterNot { it.imageUrl == imageUrl }
    }
}
