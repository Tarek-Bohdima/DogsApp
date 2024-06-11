package com.example.android.dogsapp.data.domain

import com.example.android.dogsapp.data.network.DogsApi
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

//class FetchDogsUseCase(private val dogsApi: DogsApi) {
//    sealed class Result {
//        data class Success(val dogsResponse: DogsResponse) : Result()
//        object Failure : Result()
//    }
//
//    suspend fun fetchDogs(): Result {
//        return withContext(Dispatchers.IO) {
//            try {
//                val response = dogsApi.getRandomDogs()
//                if (response.status == "success" && response.message.isNotEmpty()) {
//                    return@withContext Result.Success(response)
//                } else {
//                    return@withContext Result.Failure
//                }
//            } catch (t: Throwable) {
//                if (t is CancellationException) {
//                    return@withContext Result.Failure
//                } else {
//                    throw t
//                }
//            }
//        }
//    }
//}