package com.example.tweetssearch.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface KeywordHistoryDao {
    @Query("SELECT * FROM keywordhistory")
    fun getAll(): List<KeywordHistory>

    @Query("SELECT keyword FROM keywordhistory ORDER BY inputtime DESC LIMIT 50")
    fun loadKeywords(): List<String>

    @Insert
    fun insertAll(vararg keywordHistory: KeywordHistory)

    @Delete
    fun delete(keywordHistory: KeywordHistory)

    @Query("DELETE FROM keywordhistory WHERE keyword = :keyword")
    fun deleteByKeyword(keyword: String)

}