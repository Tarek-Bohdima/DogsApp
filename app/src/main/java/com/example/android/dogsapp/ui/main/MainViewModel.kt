package com.example.android.dogsapp.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.android.dogsapp.DogsApplication
import com.example.android.dogsapp.data.domain.Dog
import com.example.android.dogsapp.data.repository.DogsRepository
import kotlinx.coroutines.launch

enum class DogsApiStatus { LOADING, ERROR, DONE }

class MainViewModel(private val dogsRepository: DogsRepository) : ViewModel() {

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
        getRandomDogs()
    }

    private fun getRandomDogs() {
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

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as DogsApplication)
                val dogsRepository = application.appCompositionRoot.dogsPhotoRepository
                MainViewModel(dogsRepository)
            }
        }
    }
}
