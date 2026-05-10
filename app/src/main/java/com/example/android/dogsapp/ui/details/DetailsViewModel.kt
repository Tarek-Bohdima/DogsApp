package com.example.android.dogsapp.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.dogsapp.data.domain.Dog
import com.example.android.dogsapp.data.repository.DogsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val dogsRepository: DogsRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val dog: Dog = checkNotNull(savedStateHandle["dog"]) {
        "Missing 'dog' nav argument"
    }

    val isFavorite: LiveData<Boolean> = dogsRepository.isFavorite(dog.imageUrl)

    fun toggleFavorite() {
        viewModelScope.launch {
            dogsRepository.toggleFavorite(dog.imageUrl)
        }
    }
}
