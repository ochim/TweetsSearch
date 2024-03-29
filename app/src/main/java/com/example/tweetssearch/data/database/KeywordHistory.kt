package com.example.tweetssearch.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class KeywordHistory(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val keyword: String,
    val inputTime: Long
)
