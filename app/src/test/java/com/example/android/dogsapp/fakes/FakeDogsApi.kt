package com.example.android.dogsapp.fakes

import com.example.android.dogsapp.data.domain.DogsResponse
import com.example.android.dogsapp.data.network.DogsApi

class FakeDogsApi(
    var response: DogsResponse = DogsResponse(status = "success", message = emptyList()),
    var error: Throwable? = null,
) : DogsApi {
    override suspend fun getRandomDogs(): DogsResponse {
        error?.let { throw it }
        return response
    }
}
