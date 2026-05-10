package com.example.android.dogsapp.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.dogsapp.data.domain.Dog
import com.example.android.dogsapp.data.repository.DogsRepository
import com.example.android.dogsapp.ui.utils.RefreshManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class DogsApiStatus { LOADING, ERROR, DONE }

@HiltViewModel
class MainViewModel @Inject constructor(
    private val dogsRepository: DogsRepository,
    private val refreshManager: RefreshManager,
) : ViewModel() {

    private val _status = MutableStateFlow(DogsApiStatus.LOADING)
    val status: StateFlow<DogsApiStatus> = _status.asStateFlow()

    val dogs: StateFlow<List<Dog>> = dogsRepository.dogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _navigateToDetail = MutableStateFlow<Dog?>(null)
    val navigateToDetail: StateFlow<Dog?> = _navigateToDetail.asStateFlow()

    init {
        triggerRefresh()
    }

    private fun triggerRefresh() {
        viewModelScope.launch {
            _status.value = DogsApiStatus.LOADING
            _status.value = try {
                dogsRepository.refresh()
                DogsApiStatus.DONE
            } catch (e: Exception) {
                DogsApiStatus.ERROR
            }
        }
    }

    fun onDogDetailNavigated() {
        _navigateToDetail.value = null
    }

    fun onDogClicked(dog: Dog) {
        _navigateToDetail.value = dog
    }

    fun refreshDogs() {
        triggerRefresh()
        refreshManager.setRefreshing(false)
    }
}
