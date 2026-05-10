package com.example.android.dogsapp.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.dogsapp.data.domain.Dog
import com.example.android.dogsapp.data.repository.DogsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    dogsRepository: DogsRepository,
) : ViewModel() {

    val favorites: StateFlow<List<Dog>> = dogsRepository.favorites
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val isEmpty: StateFlow<Boolean> = dogsRepository.favorites
        .map { it.isEmpty() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)

    private val _navigateToDetail = MutableStateFlow<Dog?>(null)
    val navigateToDetail: StateFlow<Dog?> = _navigateToDetail.asStateFlow()

    fun onDogClicked(dog: Dog) {
        _navigateToDetail.value = dog
    }

    fun onDogDetailNavigated() {
        _navigateToDetail.value = null
    }
}
