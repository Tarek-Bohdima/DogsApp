package com.example.android.dogsapp.fakes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.dogsapp.data.local.DogDao
import com.example.android.dogsapp.data.local.DogEntity

class FakeDogDao(initial: List<DogEntity> = emptyList()) : DogDao {
    private val state = MutableLiveData<List<DogEntity>>(initial)

    override fun observeDogs(): LiveData<List<DogEntity>> = state

    override suspend fun insertAll(dogs: List<DogEntity>) {
        state.value = (state.value.orEmpty() + dogs).distinctBy { it.imageUrl }
    }

    override suspend fun clearAll() {
        state.value = emptyList()
    }
}
