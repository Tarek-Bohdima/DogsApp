package com.example.android.dogsapp.common.di.application

import android.content.Context
import com.example.android.dogsapp.DogsApplication
import com.example.android.dogsapp.data.network.DogsApi
import com.example.android.dogsapp.data.repository.DogsRepository
import com.example.android.dogsapp.data.repository.DogsRepositoryImpl
import com.example.android.dogsapp.ui.utils.RefreshManager
import com.example.android.dogsapp.ui.utils.SwipeToRefreshManagerImpl
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton


private const val BASE_URL = "https://dog.ceo/api/"

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {

    @Singleton
    @Provides
    fun provideApplication(@ApplicationContext app: Context): DogsApplication =
        app as DogsApplication

    @Singleton
    @Provides
    fun retrofit(): Retrofit {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Singleton
    @Provides
    fun provideDogsApi(retrofit: Retrofit): DogsApi {
        return retrofit.create(DogsApi::class.java)
    }

    @Provides
    fun provideDogsRepository(dogsApi: DogsApi): DogsRepository {
        return DogsRepositoryImpl(dogsApi)
    }

    @Provides
    fun provideRefreshManager(swipeToRefreshManagerImpl: SwipeToRefreshManagerImpl): RefreshManager {
        return swipeToRefreshManagerImpl
    }
}