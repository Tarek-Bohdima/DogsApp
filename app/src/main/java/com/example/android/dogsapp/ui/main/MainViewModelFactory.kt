package com.example.android.dogsapp.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.dogsapp.data.repository.DogsRepository
import com.example.android.dogsapp.ui.utils.RefreshManager
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class MainViewModelFactory @Inject constructor(
    private val dogsRepository: DogsRepository,
    private val refreshManager: RefreshManager
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(dogsRepository, refreshManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
