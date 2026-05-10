package com.example.android.dogsapp.fakes

import com.example.android.dogsapp.data.local.DogDao
import com.example.android.dogsapp.data.local.DogEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeDogDao(initial: List<DogEntity> = emptyList()) : DogDao {
    private val state = MutableStateFlow(initial)

    override fun observeDogs(): Flow<List<DogEntity>> = state.asStateFlow()

    override suspend fun insertAll(dogs: List<DogEntity>) {
        state.value = (state.value + dogs).distinctBy { it.imageUrl }
    }

    override suspend fun clearAll() {
        state.value = emptyList()
    }
}
