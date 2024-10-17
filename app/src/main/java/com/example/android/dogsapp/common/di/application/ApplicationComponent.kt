package com.example.android.dogsapp.common.di.application

import com.example.android.dogsapp.DogsApplication
import com.example.android.dogsapp.data.repository.DogsRepository
import com.example.android.dogsapp.ui.MainActivity
import com.example.android.dogsapp.ui.main.MainFragment
import dagger.Component

@ApplicationScope
@Component(modules  = [ApplicationModule::class])
interface ApplicationComponent {
    fun inject(application: DogsApplication)

    fun inject(mainActivity: MainActivity)

    fun dogsRepository(): DogsRepository
}