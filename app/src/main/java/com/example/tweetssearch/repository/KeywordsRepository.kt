package com.example.tweetssearch.repository

import com.example.tweetssearch.database.Database
import com.example.tweetssearch.database.KeywordHistory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class KeywordsRepository(private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO) {

    private val dao = Database.db?.keywordHistoryDao()

    suspend fun getRecentKeywords(): List<String>? {
        return withContext(ioDispatcher) {
            dao?.loadKeywords()
        }
    }

    suspend fun saveKeyword(keyword: String) {
        withContext(ioDispatcher) {
            dao?.deleteByKeyword(keyword)

            val history = KeywordHistory(0, keyword, System.currentTimeMillis() / 1000)
            dao?.insertAll(history)
        }
    }
}