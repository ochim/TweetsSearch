package com.example.tweetssearch.di

import android.content.Context
import com.example.tweetssearch.data.repository.AccessTokenInterface
import com.example.tweetssearch.data.repository.AccessTokenRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AccessTokenRepositoryModule {

    @Singleton
    @Provides
    fun provideAccessTokenInterface(): AccessTokenInterface =
        TwitterRepositoryModule.retrofit.create(AccessTokenInterface::class.java)

    @Singleton
    @Provides
    fun provideAccessTokenRepository(
        accessTokenInterface: AccessTokenInterface,
        @ApplicationContext context: Context
    ): AccessTokenRepository = AccessTokenRepository(Dispatchers.IO, accessTokenInterface, context)

}