package com.example.android.dogsapp.common.di.application

import android.content.Context
import androidx.room.Room
import com.example.android.dogsapp.DogsApplication
import com.example.android.dogsapp.common.imaging.CoilImageLoader
import com.example.android.dogsapp.common.imaging.ImageLoader
import com.example.android.dogsapp.data.local.DogDao
import com.example.android.dogsapp.data.local.DogsDatabase
import com.example.android.dogsapp.data.local.FavoriteDao
import com.example.android.dogsapp.data.local.MIGRATION_1_2
import com.example.android.dogsapp.data.network.DogsApi
import com.example.android.dogsapp.data.repository.DogsRepository
import com.example.android.dogsapp.data.repository.DogsRepositoryImpl
import com.example.android.dogsapp.ui.utils.RefreshManager
import com.example.android.dogsapp.ui.utils.SwipeToRefreshManagerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton


private const val BASE_URL = "https://dog.ceo/api/"
private const val DATABASE_NAME = "dogs.db"

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
        val json = Json { ignoreUnknownKeys = true }
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Singleton
    @Provides
    fun provideDogsApi(retrofit: Retrofit): DogsApi {
        return retrofit.create(DogsApi::class.java)
    }

    @Singleton
    @Provides
    fun provideDogsDatabase(@ApplicationContext context: Context): DogsDatabase =
        Room.databaseBuilder(context, DogsDatabase::class.java, DATABASE_NAME)
            .addMigrations(MIGRATION_1_2)
            .build()

    @Provides
    fun provideDogDao(database: DogsDatabase): DogDao = database.dogDao()

    @Provides
    fun provideFavoriteDao(database: DogsDatabase): FavoriteDao = database.favoriteDao()

    @Provides
    fun provideDogsRepository(
        dogsApi: DogsApi,
        dogDao: DogDao,
        favoriteDao: FavoriteDao,
    ): DogsRepository {
        return DogsRepositoryImpl(dogsApi, dogDao, favoriteDao)
    }

    @Provides
    fun provideRefreshManager(swipeToRefreshManagerImpl: SwipeToRefreshManagerImpl): RefreshManager {
        return swipeToRefreshManagerImpl
    }

    @Provides
    fun provideImageLoader(coilImageLoader: CoilImageLoader): ImageLoader = coilImageLoader
}
