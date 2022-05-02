package com.example.tweetssearch.di

import com.example.tweetssearch.data.database.KeywordHistoryDao
import com.example.tweetssearch.data.repository.KeywordsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object KeywordsRepositoryModule {

    @Provides
    @Singleton
    fun provideKeywordsRepository(
        dao: KeywordHistoryDao
    ): KeywordsRepository = KeywordsRepository(Dispatchers.IO, dao)
}