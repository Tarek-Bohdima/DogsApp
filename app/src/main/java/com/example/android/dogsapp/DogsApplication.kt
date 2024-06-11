package com.example.android.dogsapp

import android.app.Application
import com.example.android.dogsapp.common.AppCompositionRoot

class DogsApplication: Application() {

    lateinit var appCompositionRoot: AppCompositionRoot

    override fun onCreate() {
        super.onCreate()
        appCompositionRoot = AppCompositionRoot()
    }
}
