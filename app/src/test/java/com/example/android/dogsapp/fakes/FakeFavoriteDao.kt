package com.example.android.dogsapp.fakes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.dogsapp.data.local.FavoriteDao
import com.example.android.dogsapp.data.local.FavoriteEntity

class FakeFavoriteDao(initial: List<FavoriteEntity> = emptyList()) : FavoriteDao {
    private val state = MutableLiveData<List<FavoriteEntity>>(initial)
    private val perKeyState = mutableMapOf<String, MutableLiveData<Boolean>>()

    override fun observeFavorites(): LiveData<List<FavoriteEntity>> = state

    override fun isFavorite(imageUrl: String): LiveData<Boolean> =
        perKeyState.getOrPut(imageUrl) {
            MutableLiveData(state.value.orEmpty().any { it.imageUrl == imageUrl })
        }

    override suspend fun isFavoriteOnce(imageUrl: String): Boolean =
        state.value.orEmpty().any { it.imageUrl == imageUrl }

    override suspend fun add(favorite: FavoriteEntity) {
        val current = state.value.orEmpty()
        if (current.none { it.imageUrl == favorite.imageUrl }) {
            state.value = current + favorite
            perKeyState[favorite.imageUrl]?.value = true
        }
    }

    override suspend fun remove(imageUrl: String) {
        state.value = state.value.orEmpty().filterNot { it.imageUrl == imageUrl }
        perKeyState[imageUrl]?.value = false
    }
}
