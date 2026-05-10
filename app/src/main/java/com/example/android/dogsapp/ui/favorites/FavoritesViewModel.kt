package com.example.android.dogsapp.ui.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.example.android.dogsapp.data.domain.Dog
import com.example.android.dogsapp.data.repository.DogsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    dogsRepository: DogsRepository,
) : ViewModel() {

    val favorites: LiveData<List<Dog>> = dogsRepository.favorites

    val isEmpty: LiveData<Boolean> = favorites.map { it.isEmpty() }

    private val _navigateToDetail = MutableLiveData<Dog?>()
    val navigateToDetail: LiveData<Dog?>
        get() = _navigateToDetail

    fun onDogClicked(dog: Dog) {
        _navigateToDetail.value = dog
    }

    fun onDogDetailNavigated() {
        _navigateToDetail.value = null
    }
}
