package com.example.android.dogsapp.fakes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.dogsapp.data.domain.Dog
import com.example.android.dogsapp.data.repository.DogsRepository

class FakeDogsRepository(
    initialDogs: List<Dog> = emptyList(),
    initialFavorites: List<Dog> = emptyList(),
) : DogsRepository {

    private val _dogs = MutableLiveData(initialDogs)
    private val _favorites = MutableLiveData(initialFavorites)
    private val favoriteLiveData = mutableMapOf<String, MutableLiveData<Boolean>>()

    var refreshError: Throwable? = null
    var refreshCalls = 0
        private set
    var toggleCalls = 0
        private set
    val toggledUrls = mutableListOf<String>()

    override val dogs: LiveData<List<Dog>> = _dogs
    override val favorites: LiveData<List<Dog>> = _favorites

    override fun isFavorite(imageUrl: String): LiveData<Boolean> =
        favoriteLiveData.getOrPut(imageUrl) {
            MutableLiveData(_favorites.value.orEmpty().any { it.imageUrl == imageUrl })
        }

    override suspend fun toggleFavorite(imageUrl: String) {
        toggleCalls++
        toggledUrls += imageUrl
        val current = _favorites.value.orEmpty()
        _favorites.value =
            if (current.any { it.imageUrl == imageUrl }) current.filterNot { it.imageUrl == imageUrl }
            else current + Dog(imageUrl)
        favoriteLiveData[imageUrl]?.value = _favorites.value.orEmpty().any { it.imageUrl == imageUrl }
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
