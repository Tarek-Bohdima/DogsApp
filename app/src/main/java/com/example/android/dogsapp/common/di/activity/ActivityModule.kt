package com.example.android.dogsapp.common.di.activity

import com.example.android.dogsapp.ui.utils.RefreshManager
import com.example.android.dogsapp.ui.utils.SwipeToRefreshManagerImpl
import dagger.Module
import dagger.Provides

@Module
object ActivityModule {

    @Provides
    @ActivityScope
    fun provideRefreshManager(swipeToRefreshManagerImpl: SwipeToRefreshManagerImpl): RefreshManager {
        return swipeToRefreshManagerImpl
    }
}