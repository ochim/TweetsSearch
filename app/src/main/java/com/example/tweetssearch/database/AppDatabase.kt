package com.example.tweetssearch.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = arrayOf(KeywordHistory::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun keywordHistoryDao() : KeywordHistoryDao
}

object Database {
    var db : AppDatabase? = null
    private  set

    fun setDb(context: Context) {
        db = Room.databaseBuilder(
            context,
            AppDatabase::class.java, "app_database"
        ).build()
    }
}