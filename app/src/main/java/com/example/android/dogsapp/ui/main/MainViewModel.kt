package com.example.android.dogsapp.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.dogsapp.data.domain.Dog
import com.example.android.dogsapp.data.repository.DogsRepository
import com.example.android.dogsapp.ui.utils.RefreshManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class DogsApiStatus { LOADING, ERROR, DONE }

@HiltViewModel
class MainViewModel @Inject constructor(
    private val dogsRepository: DogsRepository,
    private val refreshManager: RefreshManager
) : ViewModel() {

    private val _status = MutableLiveData<DogsApiStatus>()
    val status: LiveData<DogsApiStatus>
        get() = _status

    val dogs: LiveData<List<Dog>> = dogsRepository.dogs

    private val _navigateToDetail = MutableLiveData<Dog?>()
    val navigateToDetail
        get() = _navigateToDetail

    init {
        triggerRefresh()
    }

    private fun triggerRefresh() {
        viewModelScope.launch {
            _status.value = DogsApiStatus.LOADING
            try {
                dogsRepository.refresh()
                _status.value = DogsApiStatus.DONE
            } catch (e: Exception) {
                _status.value = DogsApiStatus.ERROR
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
