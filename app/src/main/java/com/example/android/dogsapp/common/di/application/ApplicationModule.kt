package com.example.android.dogsapp.common.di.application

import android.app.Application
import com.example.android.dogsapp.data.network.DogsApi
import com.example.android.dogsapp.data.repository.DogsRepository
import com.example.android.dogsapp.data.repository.DogsRepositoryImpl
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
class ApplicationModule(private val application: Application) {

    @Provides
    @ApplicationScope
    fun retrofit(): Retrofit {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Provides
    @ApplicationScope
    fun provideDogsRepository(dogsApi: DogsApi): DogsRepository = DogsRepositoryImpl(dogsApi)

    @Provides
    @ApplicationScope
    fun application() = application

    @Provides
    @ApplicationScope
    fun dogsApi(retrofit: Retrofit): DogsApi = retrofit.create(DogsApi::class.java)

    companion object{
        private const val BASE_URL = "https://dog.ceo/api/"
    }
}
