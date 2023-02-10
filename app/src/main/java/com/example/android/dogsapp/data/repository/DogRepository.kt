package com.example.android.dogsapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.dogsapp.data.domain.Dog
import com.example.android.dogsapp.data.network.DogsApi

class DogRepository(private val dogsApi: DogsApi) {
    private val _dogsList = MutableLiveData<List<Dog>>()
    private val dogsList: LiveData<List<Dog>>
        get() = _dogsList
    private val dogsArray = ArrayList<Dog>()

    suspend fun getRandomDogs(): LiveData<List<Dog>> {
        val dogs = dogsApi.retrofitService.getRandomDogs().message
        for (dog in dogs) {
            dogsArray.add(Dog(dog))
        }
        _dogsList.value = dogsArray

        return dogsList
    }
}
