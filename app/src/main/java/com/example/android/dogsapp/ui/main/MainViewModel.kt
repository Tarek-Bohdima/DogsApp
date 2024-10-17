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

    private val _dogs = MutableLiveData<List<Dog>>()
    val dogs: LiveData<List<Dog>>
        get() = _dogs

    private val _navigateToDetail = MutableLiveData<Dog?>()
    val navigateToDetail
        get() = _navigateToDetail

    init {
        loadDogsData()
    }

    private fun loadDogsData() {
        viewModelScope.launch {
            _status.value = DogsApiStatus.LOADING
            try {
                val response = dogsRepository.getDogsPhotos()
                if (response.message.isNotEmpty() && response.status == "success") {
                    _dogs.value = response.message.map { Dog(it) }
                    _status.value = DogsApiStatus.DONE
                }else{
                    _status.value = DogsApiStatus.ERROR
                }
            } catch (e: Exception) {
                _status.value = DogsApiStatus.ERROR
                _dogs.value = ArrayList()
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
        loadDogsData()
        refreshManager.setRefreshing(false)
    }
}
