package com.example.android.dogsapp

import android.app.Application
import com.example.android.dogsapp.common.di.application.ApplicationComponent
import com.example.android.dogsapp.common.di.application.ApplicationModule
import com.example.android.dogsapp.common.di.application.DaggerApplicationComponent


class DogsApplication: Application() {

    val appComponent: ApplicationComponent by lazy {
        DaggerApplicationComponent.builder()
            .applicationModule(ApplicationModule(application = this))
            .build()
    }

    override fun onCreate() {
        super.onCreate()
    }
}
