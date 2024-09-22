package com.example.android.dogsapp.common.di.activity

import com.example.android.dogsapp.common.di.application.ApplicationComponent
import com.example.android.dogsapp.ui.MainActivity
import com.example.android.dogsapp.ui.main.MainFragment
import dagger.Component

@ActivityScope
@Component(dependencies = [ApplicationComponent::class], modules = [ActivityModule::class])
interface ActivityComponent {
    fun inject(mainActivity: MainActivity)
    fun inject(mainFragment: MainFragment)
}