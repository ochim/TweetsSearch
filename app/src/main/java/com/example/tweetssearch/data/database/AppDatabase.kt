package com.example.tweetssearch.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = arrayOf(KeywordHistory::class), version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun keywordHistoryDao() : KeywordHistoryDao
}