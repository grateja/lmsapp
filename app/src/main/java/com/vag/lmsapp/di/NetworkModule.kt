package com.vag.lmsapp.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.vag.lmsapp.adapters.ArrayListIntAdapter
import com.vag.lmsapp.adapters.InstantAdapter
import com.vag.lmsapp.adapters.UUIDAdapter
import com.vag.lmsapp.network.api.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {
    @Singleton
    @Provides
    fun provideRetrofit(moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://192.168.1.11:8000")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Singleton
    @Provides
    fun provideBranchDao(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    fun providesMoshi(): Moshi {
        return Moshi.Builder()
            .add(UUIDAdapter())
            .add(InstantAdapter())
            .add(ArrayListIntAdapter())
            .add(KotlinJsonAdapterFactory())
//            .add { type, _, moshi -> NullSafeAdapter.create<Any>(type, moshi) }
            .build()
    }
}