package com.example.android.dogsapp.fakes

import com.example.android.dogsapp.data.domain.Dog
import com.example.android.dogsapp.data.repository.DogsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class FakeDogsRepository(
    initialDogs: List<Dog> = emptyList(),
    initialFavorites: List<Dog> = emptyList(),
) : DogsRepository {

    private val _dogs = MutableStateFlow(initialDogs)
    private val _favorites = MutableStateFlow(initialFavorites)

    var refreshError: Throwable? = null
    var refreshCalls = 0
        private set
    var toggleCalls = 0
        private set
    val toggledUrls = mutableListOf<String>()

    override val dogs: Flow<List<Dog>> = _dogs.asStateFlow()
    override val favorites: Flow<List<Dog>> = _favorites.asStateFlow()

    override fun isFavorite(imageUrl: String): Flow<Boolean> =
        _favorites.map { list -> list.any { it.imageUrl == imageUrl } }

    override suspend fun toggleFavorite(imageUrl: String) {
        toggleCalls++
        toggledUrls += imageUrl
        val current = _favorites.value
        _favorites.value =
            if (current.any { it.imageUrl == imageUrl }) current.filterNot { it.imageUrl == imageUrl }
            else current + Dog(imageUrl)
    }

    override suspend fun refresh() {
        refreshCalls++
        refreshError?.let { throw it }
    }

    fun emitDogs(dogs: List<Dog>) {
        _dogs.value = dogs
    }

    fun emitFavorites(favorites: List<Dog>) {
        _favorites.value = favorites
    }
}
