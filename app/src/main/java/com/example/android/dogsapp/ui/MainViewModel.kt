package com.example.android.dogsapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.dogsapp.data.domain.Dog
import com.example.android.dogsapp.data.network.DogsApi
import com.example.android.dogsapp.data.repository.DogRepository
import kotlinx.coroutines.launch

enum class DogsApiStatus { LOADING, ERROR, DONE }

class MainViewModel : ViewModel() {

    private val dogsApi = DogsApi
    private val repository = DogRepository(dogsApi)

    private val _status = MutableLiveData<DogsApiStatus>()

    val status: LiveData<DogsApiStatus>
        get() = _status

    private val _dogs = MutableLiveData<List<Dog>>()

    val dogs: LiveData<List<Dog>>
        get() = _dogs

    init {
        getRandomDogs()
    }

    private fun getRandomDogs() {
        viewModelScope.launch {
            _status.value = DogsApiStatus.LOADING
            try {
                _dogs.value = repository.getRandomDogs().value
                _status.value = DogsApiStatus.DONE
            } catch (e: Exception) {
                _status.value = DogsApiStatus.ERROR
                _dogs.value = ArrayList()
            }
        }
    }
}
