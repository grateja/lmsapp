package com.vag.lmsapp.di

import com.vag.lmsapp.network.dao.BranchDao
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
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://192.168.1.2:8000")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun provideBranchDao(retrofit: Retrofit): BranchDao {
        return retrofit.create(BranchDao::class.java)
    }
}