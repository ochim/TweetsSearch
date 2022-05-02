package com.example.tweetssearch.di

import com.example.tweetssearch.data.repository.TweetsRemoteDataSource
import com.example.tweetssearch.data.repository.TweetsSearchInterface
import com.example.tweetssearch.data.repository.TweetsSearchRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TweetsSearchRepositoryModule {

    @Singleton
    @Provides
    fun provideTweetsSearchRepository(): TweetsSearchRepository =
        TweetsSearchRepository(
            TweetsRemoteDataSource(
                Dispatchers.IO,
                TwitterRepositoryModule.retrofit.create(TweetsSearchInterface::class.java)
            )
        )
}