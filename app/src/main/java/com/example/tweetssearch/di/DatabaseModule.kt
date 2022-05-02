package com.example.tweetssearch.di

import android.content.Context
import androidx.room.Room
import com.example.tweetssearch.data.database.AppDatabase
import com.example.tweetssearch.data.database.KeywordHistoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java, "app_database"
        ).build()

    @Singleton
    @Provides
    fun provideKeywordHistoryDao(appDatabase: AppDatabase): KeywordHistoryDao =
        appDatabase.keywordHistoryDao()
}