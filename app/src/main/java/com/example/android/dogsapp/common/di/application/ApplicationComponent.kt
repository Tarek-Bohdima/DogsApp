package com.example.android.dogsapp.common.di.application

import com.example.android.dogsapp.DogsApplication
import com.example.android.dogsapp.ui.MainActivity
import com.example.android.dogsapp.ui.main.MainFragment
import dagger.Component

@ApplicationScope
@Component(modules  = [ApplicationModule::class])
interface ApplicationComponent {
    fun inject(mainFragment: MainFragment)
    fun inject(mainActivity: MainActivity)
}