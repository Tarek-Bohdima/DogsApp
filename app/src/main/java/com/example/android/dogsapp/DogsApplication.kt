package com.example.android.dogsapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class DogsApplication: Application() {

    override fun onCreate() {
        super.onCreate()
    }
}
